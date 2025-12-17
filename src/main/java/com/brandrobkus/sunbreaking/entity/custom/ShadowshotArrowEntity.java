package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShadowshotArrowEntity extends PersistentProjectileEntity {
    private int duration = 130;
    private boolean hasSpawnedNode = false;

    public ShadowshotArrowEntity(EntityType<? extends ShadowshotArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ShadowshotArrowEntity(World world, LivingEntity owner) {
        super(ModEntities.SHADOWSHOT_ARROW, owner, world);
    }


    private ItemStack bowStack = ItemStack.EMPTY;

    public void setBowStack(ItemStack stack) {
        this.bowStack = stack.copy();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient && !this.inGround) {
            this.getWorld().addParticle(ParticleTypes.REVERSE_PORTAL,
                    this.getX(), this.getY(), this.getZ(),
                    0.0, 0.0, 0.0);
        }

        if (this.inGround && !hasSpawnedNode) {
            this.spawnNodeEntity();
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private void spawnNodeEntity() {
        if (this.getWorld().isClient || hasSpawnedNode) return;
        this.hasSpawnedNode = true;

        Vec3d velocity = this.getVelocity().normalize();
        Direction impactFace = Direction.getFacing(velocity.x, velocity.y, velocity.z).getOpposite();

        Vec3d spawnPos = this.getPos();
        double offsetDistance = 0.5;

        if (impactFace == Direction.UP || impactFace == Direction.DOWN) {
            spawnPos = spawnPos.add(0, impactFace == Direction.UP ? offsetDistance : -offsetDistance, 0);
        } else {
            spawnPos = spawnPos.add(
                    impactFace.getOffsetX() * offsetDistance,
                    impactFace.getOffsetY() * offsetDistance,
                    impactFace.getOffsetZ() * offsetDistance
            );
        }

        ShadowshotNodeEntity nodeEntity = new ShadowshotNodeEntity(ModEntities.SHADOWSHOT_NODE, this.getWorld());
        nodeEntity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

        if (this.getOwner() instanceof PlayerEntity player) {
            nodeEntity.setOwner(player);
        }

        nodeEntity.setBowStack(this.bowStack);

        this.getWorld().spawnEntity(nodeEntity);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.SHADOWSHOT_ARROW);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, this.duration, 1), this.getEffectCause());
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration, 1), this.getEffectCause());
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, this.duration - 90, 0), this.getEffectCause());

        this.spawnNodeEntity();
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Duration")) {
            this.duration = nbt.getInt("Duration");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Duration", this.duration);
    }
}