package com.brandrobkus.sunbreaking.item;

import com.brandrobkus.sunbreaking.Sunbreaking;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup HAMMER_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(Sunbreaking.MOD_ID, "hammer"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.hammer"))
                    .icon(() -> new ItemStack(ModItems.SOLAR_LIGHT)).entries((displayContext, entries) -> {

                        entries.add(ModItems.WOODEN_HAMMER);
                        entries.add(ModItems.STONE_HAMMER);
                        entries.add(ModItems.IRON_HAMMER);
                        entries.add(ModItems.GOLDEN_HAMMER);
                        entries.add(ModItems.DIAMOND_HAMMER);
                        entries.add(ModItems.NETHERITE_HAMMER);

                        entries.add(ModItems.HAMMER_OF_SOL);
                        entries.add(ModItems.HAMMER_APPEARANCE_SMITHING_TEMPLATE);
                        entries.add(ModItems.BLAZING_HAMMER);
                        entries.add(ModItems.BLAZING_APPEARANCE_SMITHING_TEMPLATE);
                        entries.add(ModItems.HELLBENT_HAMMER);
                        entries.add(ModItems.HELLBENT_APPEARANCE_SMITHING_TEMPLATE);
                        entries.add(ModItems.FLAT_HAMMER);
                        entries.add(ModItems.FLAT_APPEARANCE_SMITHING_TEMPLATE);

                        entries.add(ModItems.FRAGMENT_OF_COMBUSTION);
                        entries.add(ModItems.FRAGMENT_OF_BLISTERING);
                        entries.add(ModItems.FRAGMENT_OF_ASHES);
                        entries.add(ModItems.FRAGMENT_OF_SEARING);

                        entries.add(ModItems.SUNBREAKERS_HELMET);
                        entries.add(ModItems.SUNBREAKERS_CUIRASS);
                        entries.add(ModItems.SUNBREAKERS_GREAVES);
                        entries.add(ModItems.SUNBREAKERS_BOOTS);

                        entries.add(ModItems.ASPECT_OF_TEMPERING);
                        entries.add(ModItems.ASPECT_OF_RADIANCE);
                        entries.add(ModItems.ASPECT_OF_SOLACE);
                        entries.add(ModItems.ASPECT_OF_RESOLVE);

                        entries.add(ModItems.SOLAR_LIGHT);
                        entries.add(ModItems.SOLAR_UPGRADE_SMITHING_TEMPLATE);

                        entries.add(ModItems.SHADOWSHOT_BOW);
                        entries.add(ModItems.SHADOWSHOT_APPEARANCE_SMITHING_TEMPLATE);
                        entries.add(ModItems.WOODCARVED_SHADOWSHOT_BOW);
                        entries.add(ModItems.WOODCARVED_APPEARANCE_SMITHING_TEMPLATE);
                        entries.add(ModItems.VANILLA_SHADOWSHOT_BOW);
                        entries.add(ModItems.VANILLA_APPEARANCE_SMITHING_TEMPLATE);

                        entries.add(ModItems.SHADOWSHOT_ARROW);

                        entries.add(ModItems.FRAGMENT_OF_EXPULSION);
                        entries.add(ModItems.FRAGMENT_OF_INSTABILITY);
                        entries.add(ModItems.FRAGMENT_OF_CESSATION);
                        entries.add(ModItems.FRAGMENT_OF_VIGILANCE);

                        entries.add(ModItems.SMOKE_BOMB);

                        entries.add(ModItems.NIGHTSTALKERS_MASK);
                        entries.add(ModItems.NIGHTSTALKERS_JACKET);
                        entries.add(ModItems.NIGHTSTALKERS_PANTS);
                        entries.add(ModItems.NIGHTSTALKERS_STRIDES);

                        entries.add(ModItems.ASPECT_OF_OBSCURITY);
                        entries.add(ModItems.ASPECT_OF_EXECUTION);
                        entries.add(ModItems.ASPECT_OF_DILATION);
                        entries.add(ModItems.ASPECT_OF_RENEWAL);

                        entries.add(ModItems.VOID_LIGHT);
                        entries.add(ModItems.VOID_LIGHT_SHARD);
                        entries.add(ModItems.VOID_UPGRADE_SMITHING_TEMPLATE);

                        entries.add(ModItems.STORMCALLERS_BOND);
                        //entries.add(ModItems.ENDER_PEARL_BOND);
                        //entries.add(ModItems.FIREBALL_BOND);
                        //entries.add(ModItems.SNOWBALL_BOND);

                        entries.add(ModItems.STORMCALLERS_HEADDRESS);
                        entries.add(ModItems.STORMCALLERS_ROBES);
                        entries.add(ModItems.STORMCALLERS_PANTS);
                        entries.add(ModItems.STORMCALLERS_STEPS);

                        entries.add(ModItems.ASPECT_OF_SURGE);
                        entries.add(ModItems.ASPECT_OF_BRILLIANCE);
                        entries.add(ModItems.ASPECT_OF_IONS);
                        entries.add(ModItems.ASPECT_OF_RECHARGE);

                        entries.add(ModItems.ARC_LIGHT);
                        entries.add(ModItems.ARC_UPGRADE_SMITHING_TEMPLATE);

                        entries.add(ModItems.WOODEN_GLAIVE);
                        entries.add(ModItems.STONE_GLAIVE);
                        entries.add(ModItems.IRON_GLAIVE);
                        entries.add(ModItems.GOLD_GLAIVE);
                        entries.add(ModItems.DIAMOND_GLAIVE);
                        entries.add(ModItems.NETHERITE_GLAIVE);

                    }).build());

    public static void registerItemGroups(){
        Sunbreaking.LOGGER.info("Registering Item Groups for " + Sunbreaking.MOD_ID);
    }
}
