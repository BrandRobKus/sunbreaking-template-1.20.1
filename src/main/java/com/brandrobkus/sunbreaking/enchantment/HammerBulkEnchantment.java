package com.brandrobkus.sunbreaking.enchantment;

import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class HammerBulkEnchantment extends Enchantment implements SunbreakingEnchantment {
    public HammerBulkEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    //Hammer enchantment, increases melee damage but reduces range and speed
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.HAMMER) || stack.isIn(ModTags.Items.HAMMER_BASE);
    }

    @Override
    public int getMinPower(int level) {
        return 25;
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
    public boolean canAccept(Enchantment other){
        return !(other instanceof HammerBludgeoningEnchantment) && super.canAccept(other);
    }

}
