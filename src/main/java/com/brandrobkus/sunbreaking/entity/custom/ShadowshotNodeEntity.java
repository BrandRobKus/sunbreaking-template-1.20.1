package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.command.fireteam.FireteamManager;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.weapons.fragments.FragmentHelper;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class ShadowshotNodeEntity extends Entity {
    private int timeAlive = 0;
    public int timeToDie = 260;
    public float baseRadius = 6.0f;

    private float getNodeRadius() {
        int stacks = FragmentHelper.getFragmentCount(bowStack, ModItems.FRAGMENT_OF_EXPULSION);
        return baseRadius + (stacks * 0.5f);
    }

    private UUID ownerUUID;

    public ShadowshotNodeEntity(EntityType<? extends ShadowshotNodeEntity> type, World world) {
        super(type, world);
    }

    private static final TrackedData<Float> NODE_RADIUS =
            DataTracker.registerData(ShadowshotNodeEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Override
    public boolean isFireImmune() {
        return true;
    }

    private ItemStack bowStack = ItemStack.EMPTY;

    public void setBowStack(ItemStack stack) {
        this.bowStack = stack.copy();

        if (!getWorld().isClient) {
            this.dataTracker.set(NODE_RADIUS, getNodeRadius());
        }
    }

    private float getClientNodeRadius() {
        return this.dataTracker.get(NODE_RADIUS);
    }

    private void applyInstabilityPull() {
        if (!FragmentHelper.hasFragment(bowStack, ModItems.FRAGMENT_OF_INSTABILITY)) return;
        if (ownerUUID == null) return;

        float radius = getNodeRadius();
        Box box = new Box(
                getX() - radius, getY() - radius, getZ() - radius,
                getX() + radius, getY() + radius, getZ() + radius
        );

        List<LivingEntity> entities = getWorld().getEntitiesByClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && !e.getUuid().equals(ownerUUID)
        );

        for (LivingEntity entity : entities) {
            Vec3d toCenter = this.getPos().subtract(entity.getPos());
            double distance = toCenter.length();

            if (distance < 0.5) continue;

            double strength = (1.0 - (distance / radius)) * 0.15;
            Vec3d pull = toCenter.normalize().multiply(strength);

            entity.addVelocity(pull.x, pull.y * 0.5, pull.z);
            entity.velocityDirty = true;
        }
    }

    private void playSpawnSound() {
        getWorld().playSound(
                null,
                getBlockPos(), ModSounds.SHADOWSHOT_NODE, SoundCategory.HOSTILE, 2.0F, 1.0F
        );
    }

    public void setOwner(PlayerEntity owner) {
        this.ownerUUID = owner.getUuid();
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(NODE_RADIUS, 6.0f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    private void applyEffectsToNearbyEntities() {
        if (ownerUUID == null || getWorld().isClient) return;

        ServerPlayerEntity ownerPlayer = getWorld().getServer().getPlayerManager().getPlayer(ownerUUID);

        float radius = getCurrentRadius();
        if (radius <= 0.05f) return;

        Box effectBox = new Box(
                getX() - radius, getY() - radius, getZ() - radius,
                getX() + radius, getY() + radius, getZ() + radius
        );

        List<LivingEntity> nearbyEntities = getWorld().getEntitiesByClass(
                LivingEntity.class,
                effectBox,
                entity -> entity.isAlive()
        );

        for (LivingEntity entity : nearbyEntities) {
            if (entity.getUuid().equals(ownerUUID)) continue;
            if (ownerPlayer != null && entity instanceof ServerPlayerEntity targetPlayer) {
                if (FireteamManager.areTeammates(ownerPlayer, targetPlayer)) continue;
            }

            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3, 2, true, true, true));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 3, 0, true, true, true));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3, 0, true, true, true));
        }
    }

    @Override
    public void tick() {
        super.tick();
        timeAlive++;
        if (this.getWorld().isClient) {
            spawnParticles();
            rotateNode();
        } else {
            if (!this.getWorld().isClient && timeAlive <= 20) {
                applyInstabilityPull();
            }

            if (!this.getWorld().isClient && timeAlive >= 20) {
                applyEffectsToNearbyEntities();
            }
        }
        if (timeAlive == 1) {
            playSpawnSound();
        }
        if (timeAlive >= timeToDie) {
            explodeOnDeath();
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private float getExpansionProgress() {
        if (timeAlive < 15) {
            return 0.0f;
        } else if (timeAlive < 22) {
            return (float)(Math.sqrt(timeAlive - 15) / 2.62);
        } else if (timeAlive > 250) {
            return (float)(Math.sqrt(258 - timeAlive) / 2.82843);
        } else {
            return 1.0f;
        }
    }

    private float getCurrentRadius() {
        return getNodeRadius() * getExpansionProgress();
    }

    public float getScale() {
        double scale = 0.0;

        if (timeAlive >= 0 && timeAlive <= 10) {
            scale = (timeAlive) / 10.0;

        } else if (timeAlive >= 10 && timeAlive <= 15) {
            scale = 1.0 - ((timeAlive - 10) / 5.0) * 0.25;

        } else if (timeAlive >= 15 && timeAlive <= 20) {
            scale = 0.75 + ((timeAlive - 15) / 5.0) * 0.75;

        } else if (timeAlive >= 20 && timeAlive <= 40) {
            scale = 1.5 - ((timeAlive - 20) / 20.0) * 0.5;

        } else if (timeAlive >= 40 && timeAlive <= 200) {
            scale = 1.0 + 0.25 * Math.sin(((timeAlive - 40) / 20.0) * Math.PI);

        } else if (timeAlive >= 200 && timeAlive <= 255) {
            int x = timeAlive - 200;
            scale = 1.0 + 0.000625 * x * x;

        } else if (timeAlive >= 255 && timeAlive <= 260) {
            int x = timeAlive - 255;
            double maxScaleAt240 = 1.0 + 0.0025 * 40 * 40;
            scale = maxScaleAt240 - (maxScaleAt240 * x / 5.0);
        }

        return (float) scale;
    }

    private void explodeOnDeath() {
        if (!FragmentHelper.hasFragment(bowStack, ModItems.FRAGMENT_OF_CESSATION)) return;

        float power = 2.5f;

        getWorld().createExplosion(
                this,
                getX(), getY(), getZ(),
                power,
                World.ExplosionSourceType.NONE
        );

        Box box = new Box(getBlockPos()).expand(4.0);
        List<LivingEntity> entities = getWorld().getEntitiesByClass(
                LivingEntity.class,
                box,
                LivingEntity::isAlive
        );

        for (LivingEntity entity : entities) {
            entity.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0)
            );
        }
    }

    private static final int BASE_PARTICLES = 60;
    private static final int MAX_PARTICLES  = 220;

    private void spawnParticles() {
        World world = this.getWorld(); if (world.isClient) {
            float maxRadius = getClientNodeRadius();
            float sizeFactor = maxRadius / baseRadius;
            double maxSmallRadius = maxRadius / 12;
            double expansionProgress;

            if (timeAlive < 15) {
                expansionProgress = 0.0;
            } else if (timeAlive < 22) {
                expansionProgress = Math.sqrt(timeAlive - 15) / 2.62;
            } else if (timeAlive > 250) {
                expansionProgress = Math.sqrt(258 - timeAlive) / 2.82843;
            } else {
                expansionProgress = 1.0;
            }

            double density = Math.min(1.0, expansionProgress * 1.3);
            int particleCount = MathHelper.clamp(
                    (int)(BASE_PARTICLES * sizeFactor * sizeFactor * density),
                    10,
                    MAX_PARTICLES
            );
            int smallParticleCount = particleCount / 5;
            double currentRadius = maxRadius * expansionProgress;
            double smallSphereRadius = maxSmallRadius * expansionProgress;

            for (int i = 0; i < particleCount; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double u = Math.random() * 2 - 1;
                double phi = Math.acos(u);
                double x = getX() + currentRadius * Math.sin(phi) * Math.cos(theta);
                double y = getY() + currentRadius * Math.cos(phi);
                double z = getZ() + currentRadius * Math.sin(phi) * Math.sin(theta);
                world.addParticle
                        (new DustParticleEffect(new Vector3f(0.7f, 0.2f, 0.7f), 1.2f),
                        x, y, z, 0, 0, 0);
            }
            for (int i = 0; i < smallParticleCount / 5; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double u = Math.random() * 2 - 1; double phi = Math.acos(u);
                double x = getX() + smallSphereRadius * Math.sin(phi) * Math.cos(theta);
                double y = getY() + smallSphereRadius * Math.cos(phi);
                double z = getZ() + smallSphereRadius * Math.sin(phi) * Math.sin(theta);
                world.addParticle
                        (new DustParticleEffect(new Vector3f(0.4f, 0.1f, 0.6f), 0.7f),
                                x, y, z, 0, 0, 0);
            }
        }
    }

    private void rotateNode() {
        float scale = getScale();
        float rotationSpeed = 10.0F / (scale * scale + 0.000001F);

        float newYaw = this.getYaw() + rotationSpeed;
        this.setYaw(newYaw);
        this.setRotation(newYaw, this.getPitch());

        float newPitch = this.getPitch() + rotationSpeed;
        this.setPitch(newPitch);
        this.setRotation(newPitch, this.getPitch());
    }
}