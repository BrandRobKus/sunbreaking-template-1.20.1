package com.brandrobkus.sunbreaking.event;

import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.brandrobkus.sunbreaking.util.gui.SunbreakingSuperComponent;
import com.brandrobkus.sunbreaking.network.ModNetworking;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerTickHandler {

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickHandler::onTick);
    }

    private static void onTick(MinecraftServer server) {
        tickCounter++;

        if (tickCounter >= 1) {
            tickCounter = 0;

            server.getPlayerManager().getPlayerList().forEach(player -> {
                SunbreakingSuperComponent comp = PlayerSuperAccessor.get(player);

                comp.addSuper(0.05f);
                comp.addGear(0.2f);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(comp.getSuper());
                buf.writeFloat(comp.getGear());

                ServerPlayNetworking.send(player, ModNetworking.SUPER_GEAR_SYNC, buf);
            });
        }
    }
}
