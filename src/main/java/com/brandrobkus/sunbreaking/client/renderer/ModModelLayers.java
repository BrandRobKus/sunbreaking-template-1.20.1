package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.Sunbreaking;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer NODE =
            new EntityModelLayer(new Identifier(Sunbreaking.MOD_ID, "node"), "main");

    public static final EntityModelLayer GLAIVE_PROJECTILE =
            new EntityModelLayer(new Identifier(Sunbreaking.MOD_ID, "glaive_projectile"), "main");
}