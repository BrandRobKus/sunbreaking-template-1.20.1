package com.brandrobkus.sunbreaking.item.weapons.fragments;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragmentItem extends Item {
    public FragmentItem(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.isOf(ModItems.FRAGMENT_OF_COMBUSTION)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_fragment.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_combustion.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_combustion.tooltip_2"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_combustion.tooltip_1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_combustion.tooltip_3"));
        }

        if (stack.isOf(ModItems.FRAGMENT_OF_BLISTERING)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_fragment.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_blistering.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_blistering.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_blistering.tooltip_2").formatted(Formatting.RED));
        }

        if (stack.isOf(ModItems.FRAGMENT_OF_ASHES)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_fragment.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_ashes.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_ashes.tooltip_1"));
        }

        if (stack.isOf(ModItems.FRAGMENT_OF_SEARING)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_fragment.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_searing.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.fragment_of_searing.tooltip_1"));
        }

        else if (!stack.isIn(ModTags.Items.SOLAR_FRAGMENTS)){
            tooltip.add(Text.translatable("tooltip.sunbreaking.unregistered.tooltip").formatted(Formatting.YELLOW));
            tooltip.add(Text.translatable("tooltip.sunbreaking.unregistered.tooltip_1").formatted(Formatting.YELLOW));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
