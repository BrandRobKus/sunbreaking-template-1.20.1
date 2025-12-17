package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.Sunbreaking;
import com.brandrobkus.sunbreaking.entity.custom.ShadowshotNodeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class ShadowshotNodeRenderer extends EntityRenderer<ShadowshotNodeEntity> {
    private static final Identifier TEXTURE = new Identifier(Sunbreaking.MOD_ID, "textures/entity/shadowshot_node.png");
    private final ShadowshotNodeModel<ShadowshotNodeEntity> model;

    public ShadowshotNodeRenderer(EntityRendererFactory.Context context) {
        super(context);
        ModelPart modelPart = context.getModelLoader().getModelPart(ModModelLayers.NODE);
        this.model = new ShadowshotNodeModel<>(modelPart);
    }

    @Override
    public Identifier getTexture(ShadowshotNodeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ShadowshotNodeEntity entity,
                       float yaw, float tickDelta, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();

        //dynamic scale
        float scale = entity.getScale();
        matrixStack.scale(scale, scale, scale);

        //dynamic rotation
        matrixStack.translate(0.0, 0.0f, 0.0);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.5F);

        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }
}
