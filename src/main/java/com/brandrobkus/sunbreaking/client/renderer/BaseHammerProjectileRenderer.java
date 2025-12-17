package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.entity.custom.BaseHammerProjectileEntity;
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
public class BaseHammerProjectileRenderer extends EntityRenderer<BaseHammerProjectileEntity> {
    private final ItemRenderer itemRenderer;
    public BaseHammerProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BaseHammerProjectileEntity BaseHammerEntity,
                       float yaw,
                       float tickDelta,
                       MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers,
                       int light) {
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, BaseHammerEntity.prevYaw, BaseHammerEntity.getYaw())));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, BaseHammerEntity.prevPitch, BaseHammerEntity.getPitch())));

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));

        float rotationX = BaseHammerEntity.getRotationX();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));

        matrixStack.scale(1.75F, 1.75F, 1.75F);

        matrixStack.translate(0.0F, -0.0F, 0.0F);

        ItemStack BaseHammerItem = BaseHammerEntity.getBaseHammerStack();
        if (BaseHammerItem.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem
                (BaseHammerItem,
                        net.minecraft.client.render.model.json.ModelTransformationMode.GROUND,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        matrixStack,
                        vertexConsumers,
                        BaseHammerEntity.getWorld(),
                        0);

        matrixStack.pop();
        super.render(BaseHammerEntity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BaseHammerProjectileEntity entity) {
        return null;
    }
}
