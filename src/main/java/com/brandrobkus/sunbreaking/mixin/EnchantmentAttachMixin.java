package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.enchantment.ShadowshotEnchantment;
import com.brandrobkus.sunbreaking.enchantment.StormcallingEnchantment;
import com.brandrobkus.sunbreaking.enchantment.SunbreakingEnchantment;
import com.brandrobkus.sunbreaking.item.weapons.BaseHammerItem;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentAttachMixin {

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void onlyHams(
            int power,
            ItemStack stack,
            boolean treasureAllowed,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {

        if (!stack.isIn(ModTags.Items.HAMMER) && !stack.isIn(ModTags.Items.HAMMER_BASE)) {
            List<EnchantmentLevelEntry> enchants = cir.getReturnValue();
            enchants.removeIf(e -> e.enchantment instanceof SunbreakingEnchantment);
        }
    }

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void onlyBonds(
            int power,
            ItemStack stack,
            boolean treasureAllowed,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {

        if (!stack.isOf(ModItems.STORMCALLERS_BOND)) {
            List<EnchantmentLevelEntry> enchants = cir.getReturnValue();
            enchants.removeIf(e -> e.enchantment instanceof StormcallingEnchantment);
        }
    }

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void onlyBows(
            int power,
            ItemStack stack,
            boolean treasureAllowed,
            CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {

        if (!stack.isIn(ModTags.Items.SHADOWSHOTS)) {
            List<EnchantmentLevelEntry> enchants = cir.getReturnValue();
            enchants.removeIf(e -> e.enchantment instanceof ShadowshotEnchantment);
        }
    }
}
