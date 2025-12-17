package com.brandrobkus.sunbreaking.command.fireteam;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class FireteamEvents {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity attacker && entity instanceof ServerPlayerEntity target) {
                if (FireteamManager.areTeammates(attacker, target)) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
    }
}
