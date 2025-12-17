package com.brandrobkus.sunbreaking.item.custom.aspects;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.custom.ModVoidArmorItem;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class NightstalkingAspectHandler {
    public static void handleKill(ServerWorld world, PlayerEntity player, LivingEntity victim) {

        if (armorHasAspect(player, ModItems.ASPECT_OF_DILATION)) {
            triggerDilation(player);
        }
    }

    public static boolean armorHasAspect(PlayerEntity player, Item aspect) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        return chest.getItem() instanceof ModVoidArmorItem voidArmorItem &&
                voidArmorItem.hasItemInBundle(chest, aspect);
    }

    private static void triggerDilation(PlayerEntity player) {
        if (player.getHealth() <= 6.0f) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION,
                    1200,
                    1,
                    true, true, true
            ));
        }
    }
}
