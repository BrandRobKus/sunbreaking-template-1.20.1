package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.Sunbreaking;
import com.brandrobkus.sunbreaking.entity.custom.GlaiveProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class GlaiveProjectileRenderer extends EntityRenderer<GlaiveProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier(Sunbreaking.MOD_ID, "textures/entity/projectiles/glaive_projectile.png");
    private final GlaiveProjectileModel model;

    public GlaiveProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new GlaiveProjectileModel(context.getPart(ModModelLayers.GLAIVE_PROJECTILE));
    }

    @Override
    public void render(GlaiveProjectileEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw(tickDelta) - 90 - 90));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch(tickDelta)));

        matrices.translate(0.0, -1.4, 0.0);

        model.render(matrices, vertexConsumers.getBuffer(model.getLayer(getTexture(entity))),
                light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(GlaiveProjectileEntity entity) {
        return TEXTURE;
    }}
