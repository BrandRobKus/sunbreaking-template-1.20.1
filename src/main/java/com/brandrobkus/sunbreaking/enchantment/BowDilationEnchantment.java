package com.brandrobkus.sunbreaking.enchantment;

import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class BowDilationEnchantment extends Enchantment implements ShadowshotEnchantment {
    public BowDilationEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    //Bow enchantment, increases radius of ShadowshotNode
    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(ModItems.SHADOWSHOT_BOW);
    }
}
