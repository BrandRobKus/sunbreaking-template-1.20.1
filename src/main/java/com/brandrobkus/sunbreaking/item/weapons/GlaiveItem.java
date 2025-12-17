package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.GlaiveProjectileEntity;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlaiveItem extends SwordItem {

    private static final Set<UUID> playersOnCooldown = new HashSet<>();
    private boolean creative;
    private final float attackDamage;
    public boolean isBlockingNow;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public GlaiveItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.attackDamage = (float)attackDamage + toolMaterial.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 25;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        boolean isCreative = player.getAbilities().creativeMode;

        final String BLOCK_TICK_KEY = "sunbreaking_glaiveBlockTicks";
        final String BLOCKING_KEY = "sunbreaking_isBlocking";

        isBlockingNow = player.isUsingItem() && player.getActiveItem() == stack && player.isInSneakingPose();

        stack.getOrCreateNbt().putBoolean(BLOCKING_KEY, isBlockingNow);

        if (!isBlockingNow) {
            stack.getOrCreateNbt().putInt(BLOCK_TICK_KEY, 0);
            return;
        }

        if (!isCreative) {
            float gearValue = PlayerSuperAccessor.get(player).getGear();

            if (gearValue <= 1f) {
                player.stopUsingItem();

                player.getItemCooldownManager().set(this, 60);

                if (!world.isClient) {
                    player.playSound(ModSounds.GEAR_COOLDOWN_END, 1.0f, 1.0f);
                }

                return;
            }

            if(!player.isCreative()) {
                PlayerSuperAccessor.get(player).addGear(-0.75f);

                float newSuper = PlayerSuperAccessor.get(player).getSuper();
                float newGear = PlayerSuperAccessor.get(player).getGear();
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(newSuper);
                buf.writeFloat(newGear);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
                }
            }
        }

        if (world instanceof ServerWorld serverWorld) {
            Vec3d look = player.getRotationVec(1.0F).normalize();
            Vec3d center = player.getPos().add(look.multiply(1.1)).add(0, 1.4, 0);
            Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
            Vec3d up = new Vec3d(0, 1, 0);
            int particleCount = 10;
            double radius = 0.8;

            for (int i = 0; i < particleCount; i++) {
                double angle = serverWorld.random.nextDouble() * 2 * Math.PI;
                double r = Math.sqrt(serverWorld.random.nextDouble()) * radius;
                Vec3d offset = right.multiply(Math.cos(angle) * r).add(up.multiply(Math.sin(angle) * r));
                Vec3d pos = center.add(offset);
                Vec3d velocity = offset.normalize().multiply(0.35);

                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.x, pos.y, pos.z, 1, velocity.x, velocity.y, velocity.z, 0);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        boolean isCreative = user.getAbilities().creativeMode;

        float gearValue = PlayerSuperAccessor.get(user).getGear();

        if (user.isInSneakingPose()) {
            if (user.isUsingItem()) return TypedActionResult.pass(stack);
            if (getEnergy(stack) <= 0 && !isCreative) return TypedActionResult.fail(stack);

            user.setCurrentHand(hand);
            PlayerSuperAccessor.get(user).addGear(-0.75f);
            float newSuper = PlayerSuperAccessor.get(user).getSuper();
            float newGear = PlayerSuperAccessor.get(user).getGear();

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(newSuper);
            buf.writeFloat(newGear);

            if (user instanceof ServerPlayerEntity serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
            }

            user.incrementStat(Stats.USED.getOrCreateStat(this));

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.GLAIVE_BLOCK_START, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.consume(stack);
        }

        if (gearValue < 25f && !user.isCreative()) {
            if (world.isClient) user.playSound(ModSounds.COOLDOWN_INDICATOR, SoundCategory.PLAYERS, 1, 1);

            user.getItemCooldownManager().set(this, 15);
            playersOnCooldown.add(user.getUuid());
            return TypedActionResult.fail(stack);
        }

        PlayerSuperAccessor.get(user).addGear(-25f);

        float newSuper = PlayerSuperAccessor.get(user).getSuper();
        float newGear = PlayerSuperAccessor.get(user).getGear();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(newSuper);
        buf.writeFloat(newGear);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
        }

        if (getEnergy(stack) <= 0 && !isCreative) return TypedActionResult.fail(stack);
        if (!isCreative) drainEnergy(stack, 1);

        user.getItemCooldownManager().set(this, 20);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                ModSounds.GLAIVE_SHOT, SoundCategory.PLAYERS, 1.5F, 1.0F);

        Vec3d eyePos = user.getPos().add(0, user.getStandingEyeHeight(), 0);
        Vec3d forward = user.getRotationVec(1.0F).normalize();
        Vec3d right = new Vec3d(-forward.z, 0, forward.x).normalize();

        double forwardOffset = 0.5;
        double rightOffset = 0.3;
        double upOffset = -0.2;

        double handInversion = (hand == Hand.MAIN_HAND) ? 1 : -1;

        Vec3d spawnPos = eyePos
                .add(forward.multiply(forwardOffset))
                .add(right.multiply(rightOffset * handInversion))
                .add(0, upOffset, 0);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {

            float glaiveDamage = this.getAttackDamage();
            GlaiveProjectileEntity projectile = new GlaiveProjectileEntity(world, user, stack, glaiveDamage);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setVelocity(forward.x, forward.y, forward.z, 3F, 0.05F);
            projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
            world.spawnEntity(projectile);

            for (int i = 0; i < 5; i++) {
                double angle = serverWorld.random.nextDouble() * 2 * Math.PI;
                double r = Math.sqrt(serverWorld.random.nextDouble()) * 0.25;

                Vec3d offset = right.multiply(Math.cos(angle) * r)
                        .add(new Vec3d(0, 1, 0).multiply(Math.sin(angle) * r));

                Vec3d velocity = offset.normalize().multiply(0.35);

                serverWorld.spawnParticles(ParticleTypes.SMOKE,
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        1, velocity.x, velocity.y, velocity.z, 0.1);

                serverWorld.spawnParticles(ParticleTypes.WHITE_ASH,
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        1, velocity.x, velocity.y, velocity.z, 0.1);
            }
        }

        return TypedActionResult.consume(stack);
    }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        int cooldownTime = 30;
        player.getItemCooldownManager().set(this, cooldownTime);
        playersOnCooldown.add(user.getUuid());
    }

    public int getEnergy(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage();
    }

    public void setEnergy(ItemStack stack, int value) {
        value = Math.max(0, Math.min(stack.getMaxDamage(), value));
        int newDamage = stack.getMaxDamage() - value;

        if (newDamage != stack.getDamage()) {
            stack.setDamage(newDamage);
        }
    }

    public void drainEnergy(ItemStack stack, int amount) {
        if (!this.creative) {
            setEnergy(stack, getEnergy(stack) - amount);
        }
    }
}
