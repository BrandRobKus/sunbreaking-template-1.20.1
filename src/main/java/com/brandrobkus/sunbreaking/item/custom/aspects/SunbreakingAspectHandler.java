package com.brandrobkus.sunbreaking.item.custom.aspects;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.custom.ModSolarArmorItem;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.server.world.ServerWorld;

public class SunbreakingAspectHandler {
    public static void handleKill(ServerWorld world, PlayerEntity player, LivingEntity victim) {

        if (armorHasAspect(player, ModItems.ASPECT_OF_RADIANCE)) {
            triggerRadiance(player, victim);
        }

        if (armorHasAspect(player, ModItems.ASPECT_OF_SOLACE)) {
            triggerSolace(player);
        }

        if (armorHasAspect(player, ModItems.ASPECT_OF_RESOLVE)) {
            triggerResolve(world, player);
        }
    }

    private static boolean armorHasAspect(PlayerEntity player, Item aspect) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        return chest.getItem() instanceof ModSolarArmorItem solar &&
                solar.hasItemInBundle(chest, aspect);
    }

    private static void triggerRadiance(PlayerEntity player, LivingEntity victim) {
        if (victim.getRecentDamageSource() == null) return;

        DamageSource source = victim.getRecentDamageSource();
        if (source.getAttacker() != player) return;
        if (source.isIndirect()) return;

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                140,
                0,
                true, true, true
        ));
    }


    private static void triggerSolace(PlayerEntity player) {
        if (player.getHealth() <= 6.0f) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    140,
                    1,
                    true, true, true
            ));
        }
    }

    public static void triggerResolve(ServerWorld world, PlayerEntity player) {
        ItemStack weapon = player.getMainHandStack();

        if (weapon.isIn(ModTags.Items.HAMMER)) {
            PlayerSuperAccessor.get(player).setResolveTicks(140);
        }
    }

}
