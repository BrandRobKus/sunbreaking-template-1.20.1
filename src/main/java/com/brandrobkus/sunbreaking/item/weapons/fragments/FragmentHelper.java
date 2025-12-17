package com.brandrobkus.sunbreaking.item.weapons.fragments;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.TagKey;

public final class FragmentHelper {

    private FragmentHelper() {}

    public static int getFragmentCount(ItemStack containerStack, Item fragment) {
        if (!containerStack.hasNbt()) return 0;

        NbtCompound nbt = containerStack.getNbt();
        if (!nbt.contains("Items")) return 0;

        NbtList list = nbt.getList("Items", NbtCompound.COMPOUND_TYPE);

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.fromNbt(list.getCompound(i));
            if (stack.isOf(fragment)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasFragment(ItemStack containerStack, Item fragment) {
        return getFragmentCount(containerStack, fragment) > 0;
    }

    /* ===================== TAG-BASED ===================== */

    public static int getTaggedFragmentCount(ItemStack containerStack, TagKey<Item> tag) {
        if (!containerStack.hasNbt()) return 0;

        NbtCompound nbt = containerStack.getNbt();
        if (!nbt.contains("Items")) return 0;

        NbtList list = nbt.getList("Items", NbtCompound.COMPOUND_TYPE);

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.fromNbt(list.getCompound(i));
            if (stack.isIn(tag)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasTaggedFragment(ItemStack containerStack, TagKey<Item> tag) {
        return getTaggedFragmentCount(containerStack, tag) > 0;
    }
}
