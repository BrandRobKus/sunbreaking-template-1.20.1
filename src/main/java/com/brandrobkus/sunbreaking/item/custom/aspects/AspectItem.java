package com.brandrobkus.sunbreaking.item.custom.aspects;

import com.brandrobkus.sunbreaking.client.ModKeyBindings;
import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.text.Text;

import java.util.List;

public class AspectItem extends Item {
    public AspectItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.isOf(ModItems.ASPECT_OF_TEMPERING)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_aspect.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_tempering.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_tempering.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_tempering.tooltip_2"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_RADIANCE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_aspect.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_radiance.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_radiance.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_SOLACE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_aspect.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_solace.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_solace.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_RESOLVE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.sunbreaker_aspect.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_resolve.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_resolve.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_OBSCURITY)) {
            String keyName = ModKeyBindings.TOGGLE_INVISIBILITY.getBoundKeyLocalizedText().getString();
            tooltip.add(Text.translatable("tooltip.sunbreaking.nightstalker_aspect.tooltip")
                    .formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_obscurity.tooltip", keyName));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_obscurity.tooltip_0"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_obscurity.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_obscurity.tooltip_2"));
        }

        if (stack.isOf(ModItems.ASPECT_OF_EXECUTION)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.nightstalker_aspect.tooltip").formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_execution.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_execution.tooltip_1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_execution.tooltip_2").formatted(Formatting.RED));
        }
        if (stack.isOf(ModItems.ASPECT_OF_DILATION)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.nightstalker_aspect.tooltip").formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_dilation.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_dilation.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_RENEWAL)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.nightstalker_aspect.tooltip").formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_renewal.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_renewal.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_SURGE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.stormcaller_aspect.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_surge.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_surge.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_surge.tooltip_4"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_surge.tooltip_5"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_BRILLIANCE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.stormcaller_aspect.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_brilliance.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_brilliance.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_IONS)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.stormcaller_aspect.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_ions.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_ions.tooltip_1"));
        }
        if (stack.isOf(ModItems.ASPECT_OF_RECHARGE)) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.stormcaller_aspect.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.blank_spot.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_recharge.tooltip"));
            tooltip.add(Text.translatable("tooltip.sunbreaking.aspect_of_recharge.tooltip_1"));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
