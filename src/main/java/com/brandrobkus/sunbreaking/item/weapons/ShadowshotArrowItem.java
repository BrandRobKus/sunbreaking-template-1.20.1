package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.ShadowshotArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShadowshotArrowItem extends ArrowItem {
    public ShadowshotArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new ShadowshotArrowEntity(world, shooter);
    }
}
