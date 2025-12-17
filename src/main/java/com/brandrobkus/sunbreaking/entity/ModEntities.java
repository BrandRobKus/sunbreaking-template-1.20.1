package com.brandrobkus.sunbreaking.entity;

import com.brandrobkus.sunbreaking.Sunbreaking;
import com.brandrobkus.sunbreaking.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<SolHammerProjectileEntity> SOL_HAMMER_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "sol_hammer_projectile"),
            FabricEntityTypeBuilder.<SolHammerProjectileEntity>create(SpawnGroup.MISC, SolHammerProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<BaseHammerProjectileEntity> BASE_HAMMER_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "base_hammer_projectile"),
            FabricEntityTypeBuilder.<BaseHammerProjectileEntity>create(SpawnGroup.MISC, BaseHammerProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<ShadowshotArrowEntity> SHADOWSHOT_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "shadowshot_arrow"),
            FabricEntityTypeBuilder.<ShadowshotArrowEntity>create(SpawnGroup.MISC, ShadowshotArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<ShadowshotNodeEntity> SHADOWSHOT_NODE = Registry.register
            (Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "shadowshot_node"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, ShadowshotNodeEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 2f)).build());

    public static final EntityType<SmokeBombProjectileEntity> SMOKE_BOMB = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "smoke_bomb"),
            FabricEntityTypeBuilder.<SmokeBombProjectileEntity>create(SpawnGroup.MISC, SmokeBombProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<StormCloudEntity> STORM_CLOUD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "storm_cloud"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StormCloudEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).trackRangeBlocks(10).build());

    public static final EntityType<StormBallEntity> STORM_BALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "storm_ball"),
            FabricEntityTypeBuilder.<StormBallEntity>create(SpawnGroup.MISC, StormBallEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<GlaiveProjectileEntity> GLAIVE_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "glaive_projectile"),
            FabricEntityTypeBuilder.<GlaiveProjectileEntity>create(SpawnGroup.MISC, GlaiveProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<FirelessLightningEntity> FIRELESS_LIGHTNING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "fireless_lightning"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, FirelessLightningEntity::new)
                    .dimensions(EntityDimensions.fixed(0.0f, 0.0f)).build());

    public static final EntityType<SkyFirelessLightningEntity> SKY_FIRELESS_LIGHTNING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Sunbreaking.MOD_ID, "sky_fireless_lightning"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SkyFirelessLightningEntity::new)
                    .dimensions(EntityDimensions.fixed(0.0f, 0.0f)).build());



    public static void registerModEntities() {
        System.out.println("Registering entities for Sunbreaking Mod!");
    }
}
