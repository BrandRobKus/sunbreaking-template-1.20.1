package com.brandrobkus.sunbreaking.enchantment;

import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

//Hammer Enchantment, Loyalty but Hammers
public class HammerRecallEnchantment extends Enchantment implements SunbreakingEnchantment{
    public HammerRecallEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 5;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.HAMMER) || stack.isIn(ModTags.Items.HAMMER_BASE);
    }
}
