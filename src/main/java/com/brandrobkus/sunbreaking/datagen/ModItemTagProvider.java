package com.brandrobkus.sunbreaking.datagen;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.HAMMER)
                .add(ModItems.HAMMER_OF_SOL)
                .add(ModItems.BLAZING_HAMMER)
                .add(ModItems.HELLBENT_HAMMER)
                .add(ModItems.FLAT_HAMMER)
        ;
        getOrCreateTagBuilder(ModTags.Items.HAMMER_BASE)
                .add(ModItems.WOODEN_HAMMER)
                .add(ModItems.STONE_HAMMER)
                .add(ModItems.IRON_HAMMER)
                .add(ModItems.GOLDEN_HAMMER)
                .add(ModItems.DIAMOND_HAMMER)
                .add(ModItems.NETHERITE_HAMMER)
        ;

        getOrCreateTagBuilder(ModTags.Items.ARROWS)
                .add(Items.ARROW)
                .add(Items.SPECTRAL_ARROW)
                .add(Items.TIPPED_ARROW)
        ;
        getOrCreateTagBuilder(ModTags.Items.GLAIVES)
                .add(ModItems.WOODEN_GLAIVE)
                .add(ModItems.STONE_GLAIVE)
                .add(ModItems.IRON_GLAIVE)
                .add(ModItems.GOLD_GLAIVE)
                .add(ModItems.DIAMOND_GLAIVE)
                .add(ModItems.NETHERITE_GLAIVE)
        ;
        getOrCreateTagBuilder(ModTags.Items.BONDS)
                .add(ModItems.STORMCALLERS_BOND)
        ;
        getOrCreateTagBuilder(ModTags.Items.BONDS_BASE)
                .add(ModItems.ENDER_PEARL_BOND)
                .add(ModItems.FIREBALL_BOND)
                .add(ModItems.SNOWBALL_BOND)
        ;
        getOrCreateTagBuilder(ModTags.Items.SHADOWSHOTS)
                .add(ModItems.SHADOWSHOT_BOW)
                .add(ModItems.WOODCARVED_SHADOWSHOT_BOW)
                .add(ModItems.VANILLA_SHADOWSHOT_BOW)
        ;

        getOrCreateTagBuilder(ModTags.Items.GEAR_METER)
                .add(ModItems.WOODEN_GLAIVE)
                .add(ModItems.STONE_GLAIVE)
                .add(ModItems.IRON_GLAIVE)
                .add(ModItems.GOLD_GLAIVE)
                .add(ModItems.DIAMOND_GLAIVE)
                .add(ModItems.NETHERITE_GLAIVE)
                .add(ModItems.SMOKE_BOMB)
        ;
        getOrCreateTagBuilder(ModTags.Items.SUPER_METER)
                .add(ModItems.SHADOWSHOT_BOW)
                .add(ModItems.WOODCARVED_SHADOWSHOT_BOW)
                .add(ModItems.VANILLA_SHADOWSHOT_BOW)
                .add(ModItems.HAMMER_OF_SOL)
                .add(ModItems.BLAZING_HAMMER)
                .add(ModItems.HELLBENT_HAMMER)
                .add(ModItems.FLAT_HAMMER)
                .add(ModItems.STORMCALLERS_BOND)
        ;

        getOrCreateTagBuilder(ModTags.Items.SOLAR_ASPECTS)
                .add(ModItems.ASPECT_OF_TEMPERING)
                .add(ModItems.ASPECT_OF_RADIANCE)
                .add(ModItems.ASPECT_OF_RESOLVE)
                .add(ModItems.ASPECT_OF_SOLACE)
        ;
        getOrCreateTagBuilder(ModTags.Items.VOID_ASPECTS)
                .add(ModItems.ASPECT_OF_OBSCURITY)
                .add(ModItems.ASPECT_OF_EXECUTION)
                .add(ModItems.ASPECT_OF_RENEWAL)
                .add(ModItems.ASPECT_OF_DILATION)
        ;
        getOrCreateTagBuilder(ModTags.Items.ARC_ASPECTS)
                .add(ModItems.ASPECT_OF_SURGE)
                .add(ModItems.ASPECT_OF_BRILLIANCE)
                .add(ModItems.ASPECT_OF_IONS)
                .add(ModItems.ASPECT_OF_RECHARGE)
        ;

        getOrCreateTagBuilder(ModTags.Items.SOLAR_FRAGMENTS)
                .add(ModItems.FRAGMENT_OF_BLISTERING)
                .add(ModItems.FRAGMENT_OF_COMBUSTION)
                .add(ModItems.FRAGMENT_OF_ASHES)
                .add(ModItems.FRAGMENT_OF_SEARING)
        ;
        getOrCreateTagBuilder(ModTags.Items.VOID_FRAGMENTS)
                .add(ModItems.FRAGMENT_OF_EXPULSION)
                .add(ModItems.FRAGMENT_OF_INSTABILITY)
                .add(ModItems.FRAGMENT_OF_CESSATION)
                .add(ModItems.FRAGMENT_OF_VIGILANCE)
        ;
        getOrCreateTagBuilder(ModTags.Items.ARC_FRAGMENTS)

        ;

        getOrCreateTagBuilder(ModTags.Items.NIGHTSTALKER_ARMOR)
                .add(ModItems.NIGHTSTALKERS_MASK)
                .add(ModItems.NIGHTSTALKERS_JACKET)
                .add(ModItems.NIGHTSTALKERS_PANTS)
                .add(ModItems.NIGHTSTALKERS_STRIDES)
        ;
        getOrCreateTagBuilder(ModTags.Items.SUNBREAKER_ARMOR)
                .add(ModItems.SUNBREAKERS_HELMET)
                .add(ModItems.SUNBREAKERS_CUIRASS)
                .add(ModItems.SUNBREAKERS_GREAVES)
                .add(ModItems.SUNBREAKERS_BOOTS)
        ;
        getOrCreateTagBuilder(ModTags.Items.STORMCALLER_ARMOR)
                .add(ModItems.STORMCALLERS_HEADDRESS)
                .add(ModItems.STORMCALLERS_ROBES)
                .add(ModItems.STORMCALLERS_PANTS)
                .add(ModItems.STORMCALLERS_STEPS)
        ;
    }
}
