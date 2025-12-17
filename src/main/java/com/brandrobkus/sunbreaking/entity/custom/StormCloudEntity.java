package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.command.fireteam.FireteamManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.UUID;

public class StormCloudEntity extends ProjectileEntity {
    private int timeAlive = 0;
    private int lastLightningStrike = 0;
    private UUID ownerUUID;
    private float timeToDie = 320;
    private float detectionRadius = 4;

    public StormCloudEntity(EntityType<? extends StormCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setOwner(PlayerEntity owner) {
        this.ownerUUID = owner.getUuid();
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        timeAlive++;

        if (!getWorld().isClient && timeAlive - lastLightningStrike >= 30) {
            lastLightningStrike = timeAlive;

            if (!(getWorld() instanceof ServerWorld serverWorld)) return;

            ServerPlayerEntity ownerPlayer = null;
            if (ownerUUID != null) {
                ownerPlayer = serverWorld.getServer()
                        .getPlayerManager()
                        .getPlayer(ownerUUID);
            }

            Box detectionBox = this.getBoundingBox().expand(detectionRadius, 3, detectionRadius);

            List<LivingEntity> entities = serverWorld.getEntitiesByClass(
                    LivingEntity.class,
                    detectionBox,
                    LivingEntity::isAlive
            );

            for (LivingEntity entity : entities) {

                if (ownerUUID != null && entity.getUuid().equals(ownerUUID)) continue;

                if (entity instanceof ServerPlayerEntity targetPlayer &&
                        ownerPlayer != null &&
                        FireteamManager.areTeammates(ownerPlayer, targetPlayer)) {
                    continue;
                }

                FirelessLightningEntity lightning = new FirelessLightningEntity(
                        ModEntities.FIRELESS_LIGHTNING,
                        serverWorld
                );

                if (ownerPlayer != null) {
                    lightning.setChanneler(ownerPlayer);
                }

                lightning.setHeightDifference(this.getY() - entity.getY());
                lightning.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());

                serverWorld.spawnEntity(lightning);
                entity.emitGameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        if (getWorld().isClient) {
            for (int i = 0; i < detectionRadius * 10; i++) {
                double offsetX = random.nextGaussian() * detectionRadius / 2;
                double offsetZ = random.nextGaussian() * detectionRadius / 2;
                double offsetY = random.nextGaussian() * detectionRadius /12;

                getWorld().addParticle(ParticleTypes.SMOKE, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, 0, 0, 0);
                getWorld().addParticle(ParticleTypes.CLOUD, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, 0, 0, 0);
            }
        }

        if (timeAlive >= timeToDie) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }
}
