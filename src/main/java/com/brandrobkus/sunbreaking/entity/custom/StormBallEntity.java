package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StormBallEntity extends ThrownItemEntity {
    private final float timeToDie = 10;
    private boolean hasBounced = false;
    private int ticksSinceBounce = 0;
    private double bounceX, bounceY, bounceZ;

    public StormBallEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public StormBallEntity(LivingEntity livingEntity, World world) {
        super(ModEntities.STORM_BALL, livingEntity, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.STORM_BALL;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);

        if (hasBounced) {
            ticksSinceBounce++;

            if (!this.getWorld().isClient()) {
                if (ticksSinceBounce == timeToDie - 8) {
                    playSpawnSound();
                } else if (ticksSinceBounce >= timeToDie) {
                    spawnStormCloudAndLightning();
                    triggerExplosion();
                    this.discard();
                }
            }
        }
    }

    private void playSpawnSound() {
        this.playSound(ModSounds.STORM_CLOUD, 2.0F, 1.0F);
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        if (!hasBounced) {
            hasBounced = true;
            recordBounceLocation();
            this.setVelocity(0, 0.6, 0);
            this.setNoGravity(false);
            return;
        }
        super.onBlockHit(hit);
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        if (!hasBounced) {
            hasBounced = true;
            recordBounceLocation();
            this.setVelocity(0, 0.6, 0);
            this.setNoGravity(false);
            return;
        }
        super.onEntityHit(hit);
    }

    private void recordBounceLocation() {
        this.bounceX = this.getX();
        this.bounceY = this.getY();
        this.bounceZ = this.getZ();
    }

    private void spawnStormCloudAndLightning() {
        if (!this.getWorld().isClient()) {
            // Spawn StormCloud
            StormCloudEntity stormCloud = new StormCloudEntity(ModEntities.STORM_CLOUD, this.getWorld());
            stormCloud.refreshPositionAndAngles(bounceX, bounceY, bounceZ, this.getYaw(), this.getPitch());
            if (this.getOwner() instanceof PlayerEntity player) {
                stormCloud.setOwner(player);
            }
            this.getWorld().spawnEntity(stormCloud);

            FirelessLightningEntity lightning = new FirelessLightningEntity(ModEntities.FIRELESS_LIGHTNING, this.getWorld());
            lightning.refreshPositionAndAngles(bounceX, bounceY, bounceZ, 0, 0);
            if (this.getOwner() instanceof PlayerEntity player) {
                lightning.setChanneler((ServerPlayerEntity) player);
            }
            this.getWorld().spawnEntity(lightning);
        }
    }

    private void triggerExplosion() {
        if (!this.getWorld().isClient()) {
            BlockPos impactPos = new BlockPos(
                    (int) Math.floor(bounceX),
                    (int) Math.floor(bounceY),
                    (int) Math.floor(bounceZ)
            );
            this.getWorld().createExplosion(
                    this,
                    impactPos.getX(),
                    impactPos.getY(),
                    impactPos.getZ(),
                    2,
                    false,
                    World.ExplosionSourceType.NONE
            );
        }
    }
}
