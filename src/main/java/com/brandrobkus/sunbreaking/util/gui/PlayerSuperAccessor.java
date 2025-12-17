package com.brandrobkus.sunbreaking.util.gui;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerSuperAccessor {
    public boolean solarFireResistant;

    public static SunbreakingSuperComponent get(PlayerEntity player) {
        return ((PlayerSuperHolder) player).sunbreaking_getSuper();
    }
    public static void setResolveTicks(PlayerEntity player, int ticks) {
        get(player).setResolveTicks(ticks);
    }
    public static int getResolveTicks(PlayerEntity player) {
        return get(player).getResolveTicks();
    }

    public static void setRenewedTicks(PlayerEntity player, int ticks) {
        get(player).setRenewedTicks(ticks);
    }
    public static int getRenewedTicks(PlayerEntity player) {
        return get(player).getRenewedTicks();
    }

    public static void setRechargeTicks(PlayerEntity player, int ticks) {
        get(player).setRechargeTicks(ticks);
    }
    public static int getRechargeTicks(PlayerEntity player) {
        return get(player).getRechargeTicks();
    }
}
