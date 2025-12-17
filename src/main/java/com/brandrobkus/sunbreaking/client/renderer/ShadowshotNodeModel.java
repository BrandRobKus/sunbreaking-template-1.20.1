package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.entity.custom.ShadowshotNodeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ShadowshotNodeModel<T extends ShadowshotNodeEntity> extends SinglePartEntityModel<T> {
    private final ModelPart node;

    public ShadowshotNodeModel (ModelPart root) {
        this.node = root.getChild("node");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData node = modelPartData.addChild("node", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 8).cuboid(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(16, 0).cuboid(-1.0F, 1.0F, -3.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(16, 6).cuboid(-1.0F, -2.0F, 2.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(10, 8).cuboid(-2.0F, -1.0F, 2.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(16, 4).cuboid(-1.0F, 1.0F, 2.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(16, 2).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = node.addChild("cube_r1", ModelPartBuilder.create()
                        .uv(0, 17).cuboid(-1.0F, 9.0F, -3.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(10, 14).cuboid(-2.0F, 7.0F, -3.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(6, 17).cuboid(-1.0F, 6.0F, -3.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(12, 17).cuboid(-1.0F, 6.0F, 2.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(0, 11).cuboid(-2.0F, 7.0F, 2.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(18, 17).cuboid(-1.0F, 9.0F, 2.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r2 = node.addChild("cube_r2", ModelPartBuilder.create()
                .uv(12, 19).cuboid(-1.0F, -2.0F, -11.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-2.0F, -1.0F, -11.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 19).cuboid(-1.0F, 1.0F, -11.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 19).cuboid(-1.0F, -2.0F, -6.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 11).cuboid(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 19).cuboid(-1.0F, 1.0F, -6.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 0.0F, 0.0F, -1.5708F, 1.5708F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ShadowshotNodeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        node.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
    @Override
    public ModelPart getPart(){
        return node;
    }
}