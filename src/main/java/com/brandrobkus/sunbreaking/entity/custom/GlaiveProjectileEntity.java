package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlaiveProjectileEntity extends PersistentProjectileEntity {
    private ItemStack glaiveStack;
    private boolean dealtDamage;
    private float damage;

    public GlaiveProjectileEntity(EntityType<? extends GlaiveProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.glaiveStack = ItemStack.EMPTY;

    }

    public GlaiveProjectileEntity(World world, LivingEntity owner, ItemStack stack, float damage) {
        super(ModEntities.GLAIVE_PROJECTILE, owner, world);
        this.glaiveStack = stack.copy();
        this.setStack(this.glaiveStack);
        this.damage = damage;

    }


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    private static final TrackedData<ItemStack> STACK =
            DataTracker.registerData(GlaiveProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack.copy());
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if (this.dealtDamage) {
            this.discard();
            return;
        }

        if (this.age > 60) {
            this.discard();
        }

        if (this.getWorld().isClient) {
            double speedX = 0.1;
            double speedY = 0.1;
            double speedZ = 0.1;
            double sizeFactor = 2.0;

            this.getWorld().addParticle(ParticleTypes.WAX_OFF,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
            this.getWorld().addParticle(ParticleTypes.WHITE_ASH,
                    this.getX(), this.getY(), this.getZ(),
                    speedX * sizeFactor, speedY * sizeFactor, speedZ * sizeFactor);
        }
        super.tick();
    }

    protected ItemStack asItemStack() {
        return this.glaiveStack.copy();
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = this.damage + 2;
        if (entity instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.glaiveStack, livingEntity.getGroup());
        }

        Entity entity2 = this.getOwner();
        DamageSource damageSource = this.getDamageSources().trident(this, (entity2 == null ? this : entity2));
        this.dealtDamage = true;
        SoundEvent soundEvent = ModSounds.GLAIVE_EXPLOSION;
        if (entity.damage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity;
                if (entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
                }

                this.onHit(livingEntity2);
                this.discard();
            }
        }

        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0F;


        this.playSound(soundEvent, g, 1.0F);
    }

    public boolean hasChanneling() {
        return EnchantmentHelper.hasChanneling(this.glaiveStack);
    }

    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    protected SoundEvent getHitSound() {
        return ModSounds.GLAIVE_EXPLOSION;
    }

    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }

    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Glaive Projectile", 10)) {
            this.glaiveStack = ItemStack.fromNbt(nbt.getCompound("Glaive Projectile"));
        }

        this.dealtDamage = nbt.getBoolean("DealtDamage");
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Glaive Projectile", this.glaiveStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }

    protected float getDragInWater() {
        return 0.65F;
    }

    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
