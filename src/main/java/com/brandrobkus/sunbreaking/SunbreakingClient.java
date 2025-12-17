package com.brandrobkus.sunbreaking;


import com.brandrobkus.sunbreaking.client.ClientTickHandler;
import com.brandrobkus.sunbreaking.client.ModKeyBindings;
import com.brandrobkus.sunbreaking.client.SunbreakingClientNetworking;
import com.brandrobkus.sunbreaking.client.renderer.*;
import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.client.renderer.ModModelLayers;
import com.brandrobkus.sunbreaking.client.renderer.ShadowshotNodeModel;
import com.brandrobkus.sunbreaking.client.renderer.ShadowshotNodeRenderer;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.util.ModModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

import static com.brandrobkus.sunbreaking.client.renderer.ModModelLayers.GLAIVE_PROJECTILE;

public class SunbreakingClient implements ClientModInitializer {

    private static KeyBinding superToggle;

    @Override
    public void onInitializeClient() {

        ModModelPredicateProvider.registerModModel();

        EntityRendererRegistry.register(ModEntities.SOL_HAMMER_PROJECTILE, SolHammerProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.BASE_HAMMER_PROJECTILE, BaseHammerProjectileRenderer::new);

        EntityRendererRegistry.register(ModEntities.SHADOWSHOT_ARROW, ShadowshotArrowEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.SHADOWSHOT_NODE, ShadowshotNodeRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.NODE, ShadowshotNodeModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.SMOKE_BOMB, SmokeBombProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.GLAIVE_PROJECTILE, GlaiveProjectileRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GLAIVE_PROJECTILE, GlaiveProjectileModel::getTexturedModelData);

        /*ArmorRenderer.register(new VoidArmorRenderer(),
                ModItems.NIGHTSTALKERS_MASK,
                ModItems.NIGHTSTALKERS_JACKET,
                ModItems.NIGHTSTALKERS_PANTS,
                ModItems.NIGHTSTALKERS_STRIDES);

         */

        EntityRendererRegistry.register(ModEntities.STORM_CLOUD, StormCloudRenderer::new);
        EntityRendererRegistry.register(ModEntities.STORM_BALL, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRELESS_LIGHTNING, FirelessLightningEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKY_FIRELESS_LIGHTNING, SkyFirelessLightningRenderer::new);

        registerItemModels();
        Sunbreaking.LOGGER.info("Client initialization complete");

        ModKeyBindings.register();
        ClientTickHandler.register();
        SunbreakingClientNetworking.register();

    }
    private void registerItemModels() {
    }
}
