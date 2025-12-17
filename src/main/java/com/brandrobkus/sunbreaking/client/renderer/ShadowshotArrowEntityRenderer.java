package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.entity.custom.ShadowshotArrowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShadowshotArrowEntityRenderer extends ProjectileEntityRenderer<ShadowshotArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("sunbreaking", "textures/entity/projectiles/shadowshot_arrow.png");

    public ShadowshotArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(ShadowshotArrowEntity shadowshotArrowEntity) {
        return TEXTURE;
    }
}
