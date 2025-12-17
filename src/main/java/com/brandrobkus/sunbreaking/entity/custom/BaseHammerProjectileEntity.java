package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.enchantment.ModEnchantmentHelper;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.util.ModDamageTypes;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseHammerProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Byte> RECALL = DataTracker.registerData(BaseHammerProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(BaseHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack hammerStack;
    private boolean dealtDamage;
    private boolean hasLanded = false;
    private boolean hasBulk;
    private BlockState inBlockState;
    public int returnTimer;
    private float rotationX = 0.0f;

    public BaseHammerProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.hammerStack = ItemStack.EMPTY;
    }

    public BaseHammerProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.BASE_HAMMER_PROJECTILE, owner, world);
        this.hammerStack = stack.copy();
        this.dataTracker.set(HAMMER_STACK, stack.copy());
        this.dataTracker.set(RECALL, (byte) ModEnchantmentHelper.getRecall(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
    }

    private static final TrackedData<ItemStack> HAMMER_STACK =
            DataTracker.registerData(BaseHammerProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public ItemStack getBaseHammerStack() {
        return this.dataTracker.get(HAMMER_STACK);
    }

    private static final TrackedData<Boolean> HAS_BULK =
            DataTracker.registerData(BaseHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAMMER_STACK, ItemStack.EMPTY);
        this.dataTracker.startTracking(RECALL, (byte) 0);
        this.dataTracker.startTracking(ENCHANTED, false);
        this.dataTracker.startTracking(HAS_BULK, false);
    }

    public void setHasBulk(boolean hasBulk) {
        this.hasBulk = hasBulk;
        this.dataTracker.set(HAS_BULK, hasBulk);
    }

    @Override
    public void tick() {
        super.tick();

        this.hasBulk = this.dataTracker.get(HAS_BULK);

        if (!hasLanded) {
            this.rotationX += 20.0f;
            if (this.rotationX > 360.0f) {
                this.rotationX -= 360.0f;
            }
        }

        Entity entity = this.getOwner();
        int i = this.dataTracker.get(RECALL);
        if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.getWorld().isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.5F);
                }

                this.discard();
            } else {
                this.setNoClip(true);
                Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double) i, this.getZ());

                if (this.getWorld().isClient) {
                    this.lastRenderY = this.getY();
                }

                double d = 0.05 * (double) i;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(ModSounds.HAMMER_RETURN, 10.0F, 1.0F);
                }

                this.returnTimer++;
            }
        }
        if (this.inGroundTime > 4 && !hasLanded) {
            if (this.dataTracker.get(RECALL) == 0) {
                this.rotationX = -60.0f;
            }

            Vec3d velocity = this.getVelocity();
            if (velocity.lengthSquared() > 1.0E-6) {
                Vec3d pullBack = velocity.normalize().multiply(-0.25D);
                this.setPos(
                        this.getX() + pullBack.x,
                        this.getY() + pullBack.y,
                        this.getZ() + pullBack.z
                );
            }

            this.setVelocity(Vec3d.ZERO);
            this.hasLanded = true;
        }

        if (this.inGroundTime > 4 && !this.dealtDamage) {
            this.dealtDamage = true;
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
    }

    @Override
    protected ItemStack asItemStack() {
        return this.hammerStack.copy();
    }

    @Nullable
    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        Entity target = hit.getEntity();
        World world = this.getWorld();

        Entity owner = this.getOwner();
        float baseDamage = 8.0F;

        if (target instanceof LivingEntity livingTarget) {
            baseDamage += EnchantmentHelper.getAttackDamage(this.hammerStack, livingTarget.getGroup());
        }

        this.dealtDamage = true;
        SoundEvent soundEvent = ModSounds.HAMMER_HIT;

        RegistryEntry<DamageType> entry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(ModDamageTypes.HAMMER_STRIKE)
                .orElseThrow();

        DamageSource src = new DamageSource(
                entry,
                this,
                owner != null ? owner : this
        );

        float finalDamage = baseDamage;

        if (this.hasBulk) {
            finalDamage += 4.0F;
        } else {
            int bludgeoningLevel = EnchantmentHelper.getLevel(ModEnchantments.BLUDGEONING, this.hammerStack);
            if (bludgeoningLevel > 0) {
                finalDamage += 1.0F + (bludgeoningLevel - 1) * 0.5F;
            }
        }

        target.damage(src, finalDamage);

        if (target.getType() != EntityType.ENDERMAN && target instanceof LivingEntity livingTarget) {
            if (owner instanceof LivingEntity livingOwner) {
                EnchantmentHelper.onUserDamaged(livingTarget, owner);
                EnchantmentHelper.onTargetDamaged(livingOwner, livingTarget);
            }
            this.onHit(livingTarget);
        }

        this.setVelocity(this.getVelocity().multiply(-0.01D, -0.1D, -0.01D));
        this.playSound(soundEvent, 0.7F, 1.0F);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.inBlockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        super.onBlockHit(blockHitResult);
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(vec3d);
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
        this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shake = 7;
        this.setCritical(false);
        this.setPierceLevel((byte)0);
        this.setSound(ModSounds.HAMMER_HIT);
        this.setShotFromCrossbow(false);
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModSounds.HAMMER_HIT;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BaseHammer", NbtCompound.COMPOUND_TYPE)) {
            this.hammerStack = ItemStack.fromNbt(nbt.getCompound("BaseHammer"));
            this.dataTracker.set(HAMMER_STACK, this.hammerStack);
        } else {
            this.hammerStack = ItemStack.EMPTY;
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.rotationX = nbt.getFloat("rotationX");
        this.hasLanded = nbt.getBoolean("HasLanded");

        if (nbt.contains("RECALL", NbtCompound.BYTE_TYPE)) {
            this.dataTracker.set(RECALL, nbt.getByte("RECALL"));
        } else {
            this.dataTracker.set(RECALL, (byte) 0);
        }
        if (nbt.contains("HasBulk")) {
            this.setHasBulk(nbt.getBoolean("HasBulk"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("BaseHammer", this.getBaseHammerStack().writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);

        nbt.putFloat("rotationX", this.rotationX);
        nbt.putBoolean("HasLanded", this.hasLanded);

        nbt.putByte("RECALL", this.dataTracker.get(RECALL));
        nbt.putBoolean("HasBulk", this.hasBulk);
    }

    @Override
    public void age() {
        int i = this.dataTracker.get(RECALL);
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }
    }

    @Override
    protected float getDragInWater() {
        return 0.75F;
    }
    
    public float getRotationX() {
        return this.rotationX;
    }
}
