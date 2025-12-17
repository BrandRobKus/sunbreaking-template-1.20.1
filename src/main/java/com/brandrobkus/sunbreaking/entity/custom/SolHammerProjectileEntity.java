package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.enchantment.ModEnchantmentHelper;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.util.ModDamageTypes;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SolHammerProjectileEntity extends PersistentProjectileEntity{
    private static final TrackedData<Byte> RECALL = DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack hammerStack;
    private boolean dealtDamage;
    private boolean hasLanded = false;
    private boolean hasBulk;

    public int returnTimer;
    private float rotationZ = 0.0f;

    public SolHammerProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.hammerStack = ItemStack.EMPTY;
    }

    public SolHammerProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.SOL_HAMMER_PROJECTILE, owner, world);
        this.hammerStack = stack.copy();
        this.dataTracker.set(HAMMER_STACK, stack.copy());
        this.dataTracker.set(RECALL, (byte) ModEnchantmentHelper.getRecall(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());

        this.dataTracker.startTracking(COMBUSTION, 0);
        this.dataTracker.startTracking(BLISTERING, false);
        this.dataTracker.startTracking(SEARING, false);
        this.dataTracker.startTracking(ASHES, false);

    }

    private static final TrackedData<ItemStack> HAMMER_STACK =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Boolean> HAS_BULK =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COMBUSTION =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> BLISTERING =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEARING =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ASHES =
            DataTracker.registerData(SolHammerProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public ItemStack getSolHammerStack() {
        return this.dataTracker.get(HAMMER_STACK);
    }

    public void setFragmentData(int combustion, boolean blistering, boolean searing, boolean ashes) {
        this.dataTracker.set(COMBUSTION, combustion);
        this.dataTracker.set(BLISTERING, blistering);
        this.dataTracker.set(SEARING, searing);
        this.dataTracker.set(ASHES, ashes);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAMMER_STACK, ItemStack.EMPTY);
        this.dataTracker.startTracking(RECALL, (byte) 0);
        this.dataTracker.startTracking(ENCHANTED, false);
        this.dataTracker.startTracking(HAS_BULK, false);
    }

    public boolean hasBulk() {
        return this.dataTracker.get(HAS_BULK);
    }

    public void setHasBulk(boolean hasBulk) {
        this.hasBulk = hasBulk;
        this.dataTracker.set(HAS_BULK, hasBulk);
    }

    @Override
    public void tick() {
        if (!hasLanded) {
            this.rotationZ += 20.0f;
            if (this.rotationZ > 360.0f) {
                this.rotationZ -= 360.0f;
            }
        }

        Entity entity = this.getOwner();
        int i = this.dataTracker.get(RECALL);
        if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.getWorld().isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
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
            this.triggerExplosion();

            if (this.dataTracker.get(RECALL) == 0) {
                this.rotationZ = 30.0f;
            }
            this.setVelocity(0, 0, 0);
            this.hasLanded = true;
        }
        if (this.inGroundTime > 4 && !this.dealtDamage) {
            this.dealtDamage = true;
        }

        if (this.getWorld().isClient && !hasLanded) {
            double speedX = 0.0;
            double speedY = 0.0;
            double speedZ = 0.0;
            double sizeFactor = 2.0;

            this.getWorld().addParticle(ParticleTypes.WHITE_ASH,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.SMALL_FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.LAVA,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
        } else {
            double speedX = 0.0;
            double speedY = 0.0;
            double speedZ = 0.0;
            double sizeFactor = 2.0;

            this.getWorld().addParticle(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
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

    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        float baseDamage = 8.0F;

        if (target instanceof LivingEntity livingTarget) {
            baseDamage += EnchantmentHelper.getAttackDamage(this.hammerStack, livingTarget.getGroup());
        }

        Entity owner = this.getOwner();

        RegistryEntry<DamageType> entry = getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(ModDamageTypes.HAMMER_STRIKE)
                .orElseThrow();

        DamageSource src = new DamageSource(entry, this, owner != null ? owner : this);

        float finalDamage = baseDamage;
        if (this.hasBulk()) {
            finalDamage += 4.0F;
        } else {
            int bludgeoningLevel = EnchantmentHelper.getLevel(ModEnchantments.BLUDGEONING, this.hammerStack);
            if (bludgeoningLevel > 0) {
                finalDamage += 1.0F + (bludgeoningLevel - 1) * 0.5F;
            }
        }

        this.dealtDamage = true;
        SoundEvent soundEvent = ModSounds.HAMMER_HIT;

        if (target.damage(src, finalDamage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (target instanceof LivingEntity livingTarget2 && owner instanceof LivingEntity livingOwner) {
                EnchantmentHelper.onUserDamaged(livingTarget2, livingOwner);
                EnchantmentHelper.onTargetDamaged(livingOwner, livingTarget2);
                this.onHit(livingTarget2);
            }
        }

        this.triggerExplosion();
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        this.playSound(soundEvent, 0.7F, 1.0F);
    }

    private void triggerExplosion() {
        if (this.getWorld().isClient) return;

        int combustion = this.dataTracker.get(COMBUSTION);
        boolean blistering = this.dataTracker.get(BLISTERING);

        float radius = 2.0F + combustion;

        BlockPos pos = this.getBlockPos();

        if (blistering) {
            igniteArea(pos, radius);
        }

        this.getWorld().createExplosion(
                this,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                radius,
                false,
                World.ExplosionSourceType.NONE
        );

        if (this.dataTracker.get(ASHES)) {
            spawnAshesProjectile(pos);
        }
    }

    private void igniteArea(BlockPos center, float radius) {
        if (this.getWorld().isClient) return;

        World world = this.getWorld();
        int r = MathHelper.ceil(radius + 1.5f);

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double distSq = x * x + z * z;
                if (distSq > radius * radius) continue;

                for (int y = -1; y <= 1; y++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockPos above = pos.up();

                    if (world.getBlockState(above).isAir()
                            && world.getBlockState(pos).isSolidBlock(world, pos)) {
                        world.setBlockState(above, net.minecraft.block.Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }

    private void spawnAshesProjectile(BlockPos pos) {
        for (int i = 0; i < 3; i++) {
            PersistentProjectileEntity arrow =
                    new net.minecraft.entity.projectile.ArrowEntity(this.getWorld(), pos.getX(), pos.getY(), pos.getZ());

            arrow.setVelocity(
                    (this.random.nextDouble() - 0.5) * 0.8,
                    0.4,
                    (this.random.nextDouble() - 0.5) * 0.8
            );

            this.getWorld().spawnEntity(arrow);
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
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
        if (nbt.contains("HammerOfSol", NbtCompound.COMPOUND_TYPE)) {
            this.hammerStack = ItemStack.fromNbt(nbt.getCompound("HammerOfSol"));
            this.dataTracker.set(HAMMER_STACK, this.hammerStack);
        } else {
            this.hammerStack = ItemStack.EMPTY;
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.rotationZ = nbt.getFloat("RotationZ");
        this.hasLanded = nbt.getBoolean("HasLanded");

        if (nbt.contains("RECALL", NbtCompound.BYTE_TYPE)) {
            this.dataTracker.set(RECALL, nbt.getByte("RECALL"));
        } else {
            this.dataTracker.set(RECALL, (byte) 0);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("HammerOfSol", this.hammerStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);

        nbt.putFloat("RotationZ", this.rotationZ);
        nbt.putBoolean("HasLanded", this.hasLanded);

        nbt.putByte("RECALL", this.dataTracker.get(RECALL));
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

    public float getRotationZ() {
        return this.rotationZ;
    }
}