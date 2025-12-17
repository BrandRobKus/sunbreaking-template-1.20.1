package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.entity.custom.SkyFirelessLightningEntity;
import com.brandrobkus.sunbreaking.item.custom.ModArcArmorItem;
import com.brandrobkus.sunbreaking.item.custom.aspects.NightstalkingAspectHandler;
import com.brandrobkus.sunbreaking.item.custom.aspects.StormcallingAspectHandler;
import com.brandrobkus.sunbreaking.item.custom.aspects.SunbreakingAspectHandler;
import com.brandrobkus.sunbreaking.item.weapons.GlaiveItem;
import com.brandrobkus.sunbreaking.util.ServerScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "onKilledOther", at = @At("HEAD"))
    private void onKilledOther(ServerWorld world, LivingEntity victim, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        SunbreakingAspectHandler.handleKill(world, player, victim);
        NightstalkingAspectHandler.handleKill(world, player, victim);
        StormcallingAspectHandler.handleKill(world, player, victim, false);
    }
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        System.out.println("[Sunbreaking] ATTACK MIXIN TRIGGERED");
        if (!(target instanceof LivingEntity livingTarget)) return;

        LivingEntity attacker = (LivingEntity)(Object)this;
        if (!(attacker instanceof PlayerEntity player)) return;

        ItemStack chest = player.getInventory().getArmorStack(2);
        if (!(chest.getItem() instanceof ModArcArmorItem)) return;

        NbtCompound nbt = chest.getOrCreateNbt();
        boolean hasSpeed2 = nbt.getBoolean("aspectSpeed2");

        if (!hasSpeed2) return;
        if (player.getWorld().isClient()) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        nbt.putBoolean("aspectSpeed", false);
        nbt.putBoolean("aspectSpeed2", false);
        nbt.putInt("sprintTime", 0);

        LivingEntity trackedTarget = livingTarget;

        ServerScheduler.schedule(15, () -> {

            if (world.isClient()) return;
            if (trackedTarget == null || trackedTarget.isRemoved() || trackedTarget.isDead()) return;

            double x = trackedTarget.getX();
            double y = trackedTarget.getY();
            double z = trackedTarget.getZ();

            SkyFirelessLightningEntity lightning = ModEntities.SKY_FIRELESS_LIGHTNING.create(world);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(x, y, z);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    lightning.setChanneler(serverPlayer);
                }
                world.spawnEntity(lightning);
            }
        });
    }
}
