package com.brandrobkus.sunbreaking.item.custom.aspects;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.custom.ModArcArmorItem;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class StormcallingAspectHandler {
    public static void handleKill(ServerWorld world, PlayerEntity player, LivingEntity victim, boolean ignoreWeaponCheck) {
        if (armorHasAspect(player, ModItems.ASPECT_OF_RECHARGE)) {
            triggerRecharge(world, player, ignoreWeaponCheck);
        }
    }

    private static boolean armorHasAspect(PlayerEntity player, Item aspect) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        return chest.getItem() instanceof ModArcArmorItem arcArmorItem &&
                arcArmorItem.hasItemInBundle(chest, aspect);
    }

    public static void triggerRecharge(ServerWorld world, PlayerEntity player, boolean isLightningKill) {
        if (isLightningKill || player.getMainHandStack().isIn(ModTags.Items.BONDS)) {
            PlayerSuperAccessor.get(player).setRechargeTicks(140);
        }
    }

}
