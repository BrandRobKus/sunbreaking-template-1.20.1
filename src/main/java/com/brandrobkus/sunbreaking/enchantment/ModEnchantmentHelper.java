package com.brandrobkus.sunbreaking.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class ModEnchantmentHelper extends EnchantmentHelper {
    public ModEnchantmentHelper() {
    }

    public static int getRecall(ItemStack stack) {
        return getLevel(ModEnchantments.RECALL, stack);
    }
}
