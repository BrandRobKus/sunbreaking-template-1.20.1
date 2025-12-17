package com.brandrobkus.sunbreaking.datagen;


import com.brandrobkus.sunbreaking.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.STORM_BALL, Models.GENERATED);

        itemModelGenerator.register(ModItems.ASPECT_OF_TEMPERING, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_RADIANCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_SOLACE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_RESOLVE, Models.GENERATED);

        itemModelGenerator.register(ModItems.ASPECT_OF_OBSCURITY, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_EXECUTION, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_DILATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_RENEWAL, Models.GENERATED);

        itemModelGenerator.register(ModItems.ASPECT_OF_SURGE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_BRILLIANCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_IONS, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASPECT_OF_RECHARGE, Models.GENERATED);

        itemModelGenerator.register(ModItems.FRAGMENT_OF_COMBUSTION, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_BLISTERING, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_ASHES, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_SEARING, Models.GENERATED);

        itemModelGenerator.register(ModItems.FRAGMENT_OF_EXPULSION, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_CESSATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_INSTABILITY, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRAGMENT_OF_VIGILANCE, Models.GENERATED);

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUNBREAKERS_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUNBREAKERS_CUIRASS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUNBREAKERS_GREAVES));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUNBREAKERS_BOOTS));

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.NIGHTSTALKERS_MASK));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.NIGHTSTALKERS_JACKET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.NIGHTSTALKERS_PANTS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.NIGHTSTALKERS_STRIDES));

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.STORMCALLERS_HEADDRESS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.STORMCALLERS_ROBES));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.STORMCALLERS_PANTS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.STORMCALLERS_STEPS));
    }
}
