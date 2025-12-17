package com.brandrobkus.sunbreaking.entity.custom;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.custom.ModArcArmorItem;
import com.google.common.collect.Sets;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class SkyFirelessLightningEntity extends Entity {

    private double heightDifference = 0;
    private int ambientTick;
    public long seed;
    private int remainingActions;
    private boolean cosmetic;
    @Nullable
    private ServerPlayerEntity channeler;
    private final Set<Entity> struckEntities = Sets.<Entity>newHashSet();

    public SkyFirelessLightningEntity(EntityType<? extends SkyFirelessLightningEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
        this.ambientTick = 2;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(3) + 1;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    private void powerLightningRod() {
        BlockPos blockPos = this.getAffectedBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock) blockState.getBlock()).setPowered(blockState, this.getWorld(), blockPos);
        }
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        super.onStruckByLightning(world, lightning);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ambientTick == 2) {
            if (this.getWorld().isClient()) {
                this.getWorld()
                        .playSound(
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                                SoundCategory.WEATHER,
                                15,
                                0.8F + this.random.nextFloat() * 0.2F,
                                false
                        );
                this.getWorld()
                        .playSound(
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                                SoundCategory.WEATHER,
                                2.0F,
                                0.5F + this.random.nextFloat() * 0.2F,
                                false
                        );
            } else {
                this.powerLightningRod();
                cleanOxidation(this.getWorld(), this.getAffectedBlockPos());
                this.emitGameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        this.ambientTick--;
        if (this.ambientTick < 0) {
            if (this.remainingActions == 0) {
                if (this.getWorld() instanceof ServerWorld) {
                    List<Entity> list = this.getWorld()
                            .getOtherEntities(
                                    this,
                                    new Box(this.getX() - 15.0, this.getY() - 15.0, this.getZ() - 15.0, this.getX() + 15.0, this.getY() + 6.0 + 15.0, this.getZ() + 15.0),
                                    entityx -> entityx.isAlive() && !this.struckEntities.contains(entityx)
                            );

                    for (ServerPlayerEntity serverPlayerEntity : ((ServerWorld) this.getWorld())
                            .getPlayers(serverPlayerEntityx -> serverPlayerEntityx.distanceTo(this) < 256.0F)) {
                        for (Entity struckEntity : list) {
                            if (struckEntity instanceof ServerPlayerEntity player) {
                                Criteria.LIGHTNING_STRIKE.trigger(player, null, list);
                            }
                        }
                    }
                }

                this.discard();
            } else if (this.ambientTick < -this.random.nextInt(10)) {
                this.remainingActions--;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();
            }
        }

        if (this.ambientTick >= 0) {
            if (!(this.getWorld() instanceof ServerWorld)) {
                this.getWorld().setLightningTicksLeft(2);
            } else if (!this.cosmetic) {
                List<Entity> list = this.getWorld()
                        .getOtherEntities(
                                this, new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0), Entity::isAlive
                        );

                for (Entity entity : list) {
                    if (entity instanceof LivingEntity livingEntity) {

                        livingEntity.damage(getWorld().getDamageSources().lightningBolt(), 9.0F);

                        if (livingEntity instanceof PlayerEntity player) {
                            ModArcArmorItem.tryApplyIonsResistanceIfEquipped(player);
                        }

                        if (this.channeler != null && channelerHasBrilliance()) {

                            // Skip effect if the victim *is* the channeler (the summoner).
                            if (livingEntity instanceof PlayerEntity player &&
                                    player.getUuid().equals(this.channeler.getUuid())) {
                                // ignore the creator entirely
                            } else {
                                livingEntity.addStatusEffect(
                                        new StatusEffectInstance(
                                                StatusEffects.BLINDNESS,
                                                45,
                                                0,
                                                false,
                                                true,
                                                true
                                        )
                                );
                            }
                        }

                    }
                }

            }
        }
    }

    public void setChanneler(ServerPlayerEntity player) {
        this.channeler = player;
    }

    private BlockPos getAffectedBlockPos() {
        Vec3d vec3d = this.getPos();
        return BlockPos.ofFloored(vec3d.x, vec3d.y - 1.0E-6, vec3d.z);
    }

    private static void cleanOxidation(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        BlockPos blockPos;
        BlockState blockState2;
        if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
            blockPos = pos.offset((blockState.get(LightningRodBlock.FACING)).getOpposite());
            blockState2 = world.getBlockState(blockPos);
        } else {
            blockPos = pos;
            blockState2 = blockState;
        }

        if (blockState2.getBlock() instanceof Oxidizable) {
            world.setBlockState(blockPos, Oxidizable.getUnaffectedOxidationState(world.getBlockState(blockPos)));
            BlockPos.Mutable mutable = pos.mutableCopy();
            int i = world.random.nextInt(3) + 3;

            for (int j = 0; j < i; j++) {
                int k = world.random.nextInt(8) + 1;
                cleanOxidationAround(world, blockPos, mutable, k);
            }
        }
    }

    private static void cleanOxidationAround(World world, BlockPos pos, BlockPos.Mutable mutablePos, int count) {
        mutablePos.set(pos);

        for (int i = 0; i < count; i++) {
            Optional<BlockPos> optional = cleanOxidationAround(world, mutablePos);
            if (!optional.isPresent()) {
                break;
            }

            mutablePos.set((Vec3i) optional.get());
        }
    }

    private static Optional<BlockPos> cleanOxidationAround(World world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterateRandomly(world.random, 10, pos, 1)) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock() instanceof Oxidizable) {
                Oxidizable.getDecreasedOxidationState(blockState).ifPresent(state -> world.setBlockState(blockPos, state));
                world.syncWorldEvent(WorldEvents.ELECTRICITY_SPARKS, blockPos, -1);
                return Optional.of(blockPos);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = 64.0 * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    private boolean channelerHasBrilliance() {
        if (this.channeler == null) return false;

        ItemStack chestplate = channeler.getInventory().getArmorStack(2);
        if (!(chestplate.getItem() instanceof ModArcArmorItem armor)) return false;

        return ModArcArmorItem.hasItemInBundle(chestplate, ModItems.ASPECT_OF_BRILLIANCE);
    }


    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putDouble("HeightDifference", this.heightDifference);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("HeightDifference")) {
            this.heightDifference = nbt.getDouble("HeightDifference");
        }
    }

    public Stream<Entity> getStruckEntities() {
        return this.struckEntities.stream().filter(Entity::isAlive);
    }
}