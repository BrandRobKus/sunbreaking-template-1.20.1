package com.brandrobkus.sunbreaking.event;

import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.brandrobkus.sunbreaking.util.gui.SunbreakingSuperComponent;
import com.brandrobkus.sunbreaking.network.ModNetworking;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class DamageTracker {

    public static void register() {

        AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
            if (!(target instanceof LivingEntity)) return ActionResult.PASS;

            if (!world.isClient()) {
                float damage = (float) player.getAttributeValue(
                        net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE
                );

                SunbreakingSuperComponent comp = PlayerSuperAccessor.get(player);

                comp.addSuper(damage);
                comp.addGear(damage * 2f);

                if (player instanceof ServerPlayerEntity serverPlayer) {
                    sendSyncPacket(serverPlayer, comp);
                }

            }

            return ActionResult.PASS;
        });
    }

    public static void onProjectileHit(LivingEntity target, PersistentProjectileEntity projectile, float damage) {
        if (!target.getWorld().isClient()) {
            if (projectile.getOwner() instanceof ServerPlayerEntity player) {
                SunbreakingSuperComponent comp = PlayerSuperAccessor.get(player);

                comp.addSuper(damage * 0.5f);
                comp.addGear(damage * 1f);

                sendSyncPacket(player, comp);
            }
        }
    }

    private static void sendSyncPacket(ServerPlayerEntity player, SunbreakingSuperComponent comp) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(comp.getSuper());
        buf.writeFloat(comp.getGear());
        ServerPlayNetworking.send(player, ModNetworking.SUPER_GEAR_SYNC, buf);
    }
}
