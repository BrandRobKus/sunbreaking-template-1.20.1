package com.brandrobkus.sunbreaking.util;

import com.brandrobkus.sunbreaking.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModModelPredicateProvider {

    public static void registerShadowShotBow (Item bow){

        FabricModelPredicateProviderRegistry.register(ModItems.SHADOWSHOT_BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(
                ModItems.SHADOWSHOT_BOW,
                new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }

    public static void registerWoodCarvedShadowShotBow (Item bow){

        FabricModelPredicateProviderRegistry.register(ModItems.WOODCARVED_SHADOWSHOT_BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(
                ModItems.WOODCARVED_SHADOWSHOT_BOW,
                new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }

    public static void registerVanillaShadowShotBow (Item bow){

        FabricModelPredicateProviderRegistry.register(ModItems.VANILLA_SHADOWSHOT_BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(
                ModItems.VANILLA_SHADOWSHOT_BOW,
                new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }
    public static void registerModModel(){
        registerShadowShotBow(ModItems.SHADOWSHOT_BOW);
        registerWoodCarvedShadowShotBow(ModItems.WOODCARVED_SHADOWSHOT_BOW);
        registerVanillaShadowShotBow(ModItems.VANILLA_SHADOWSHOT_BOW);
    }
}