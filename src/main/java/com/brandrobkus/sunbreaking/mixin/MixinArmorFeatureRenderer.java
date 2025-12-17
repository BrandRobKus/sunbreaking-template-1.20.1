package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.item.custom.ModVoidArmorItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIIFF)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void renderCosmeticArmor(T entity, float f, float g, MatrixStack matrices,
                                     VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        if (entity == net.minecraft.client.MinecraftClient.getInstance().player) return;

        if (entity.hasStatusEffect(StatusEffects.INVISIBILITY) && isWearingVoidArmor(entity)) {
            ci.cancel();
        }
    }

    private boolean isWearingVoidArmor(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorItems()) {
            if (stack.getItem() instanceof ModVoidArmorItem) return true;
        }
        return false;
    }
}
