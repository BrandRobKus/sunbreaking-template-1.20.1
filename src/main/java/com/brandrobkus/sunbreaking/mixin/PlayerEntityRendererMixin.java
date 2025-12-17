package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.util.ModTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends
        LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    PlayerEntityRendererMixin(EntityRendererFactory.Context context,
                              PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void setGlaiveHoldingPose(AbstractClientPlayerEntity player, Hand hand,
                                             CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isIn(ModTags.Items.GLAIVES)) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
        }
    }
    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void setBondHoldingPose(AbstractClientPlayerEntity player, Hand hand,
                                             CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isIn(ModTags.Items.BONDS)) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        }
    }
}
