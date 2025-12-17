package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.entity.custom.SolHammerProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SolHammerProjectileRenderer extends EntityRenderer<SolHammerProjectileEntity> {
    private final ItemRenderer itemRenderer;
    public SolHammerProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SolHammerProjectileEntity solHammerEntity,
                       float yaw,
                       float tickDelta,
                       MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers,
                       int light) {
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, solHammerEntity.prevYaw, solHammerEntity.getYaw()) - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, solHammerEntity.prevPitch, solHammerEntity.getPitch())));

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0F));

        float rotationZ = solHammerEntity.getRotationZ();
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ));

        matrixStack.translate(0.1F, -0.3F, 0.0F);
        matrixStack.scale(1.75F, 1.75F, 1.75F);

        ItemStack SolHammerItem = solHammerEntity.getSolHammerStack();
        if (SolHammerItem.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem(SolHammerItem, net.minecraft.client.render.model.json.ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumers, solHammerEntity.getWorld(), 0);


        matrixStack.pop();
        super.render(solHammerEntity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SolHammerProjectileEntity entity) {
        return null;
    }
}
