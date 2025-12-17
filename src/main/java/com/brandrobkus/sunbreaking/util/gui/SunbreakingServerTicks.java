package com.brandrobkus.sunbreaking.util.gui;

import com.brandrobkus.sunbreaking.network.ModNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class SunbreakingServerTicks {

    public static void register() {

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClient) return;

            for (ServerPlayerEntity player : world.getPlayers()) {
                tickRenewal(player);
            }
        });
    }

    private static void tickRenewal(ServerPlayerEntity player) {
        SunbreakingSuperComponent comp = PlayerSuperAccessor.get(player);

        int ticks = comp.getRenewedTicks();
        if (ticks > 0) {
            comp.setRenewedTicks(ticks - 1);

            comp.addSuper(0.1f);

            syncSuper(player, comp);
        }
    }

    private static void syncSuper(ServerPlayerEntity player, SunbreakingSuperComponent comp) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(comp.getSuper());
        buf.writeFloat(comp.getGear());
        ServerPlayNetworking.send(player, ModNetworking.SUPER_GEAR_SYNC, buf);
    }
}
