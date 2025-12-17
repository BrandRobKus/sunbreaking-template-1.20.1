package com.brandrobkus.sunbreaking.client;

import com.brandrobkus.sunbreaking.network.ModNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class ClientTickHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeyBindings.TOGGLE_INVISIBILITY.wasPressed()) {
                ClientPlayNetworking.send(
                        ModNetworking.TOGGLE_INVISIBILITY,
                        PacketByteBufs.empty()
                );
            }
        });

    }
}
