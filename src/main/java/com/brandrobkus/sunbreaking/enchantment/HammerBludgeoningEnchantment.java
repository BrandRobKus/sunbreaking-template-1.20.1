package com.brandrobkus.sunbreaking.enchantment;

import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class HammerBludgeoningEnchantment extends Enchantment implements SunbreakingEnchantment {
    public HammerBludgeoningEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.HAMMER) || stack.isIn(ModTags.Items.HAMMER_BASE);
    }

    @Override
    public boolean canAccept(Enchantment other){
        return !(other instanceof HammerBulkEnchantment) && super.canAccept(other);
    }
}
