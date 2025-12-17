package com.brandrobkus.sunbreaking.client.renderer;

import com.brandrobkus.sunbreaking.entity.custom.GlaiveProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;


public class GlaiveProjectileModel extends EntityModel<GlaiveProjectileEntity> {
	private final ModelPart bb_main;

	public GlaiveProjectileModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("bb_main", ModelPartBuilder.create()
				.uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(8, 6).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(12, 0).cuboid(1.0F, -1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(12, 3).cuboid(-2.0F, -1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 10).cuboid(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 6).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(12, 11).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 13).cuboid(-0.5F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(12, 13).cuboid(1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 14).cuboid(-2.0F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(6, 11).cuboid(-0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(GlaiveProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}