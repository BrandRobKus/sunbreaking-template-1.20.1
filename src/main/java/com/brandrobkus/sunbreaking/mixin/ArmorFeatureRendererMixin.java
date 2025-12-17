package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void invisMode(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            T entity,
            EquipmentSlot armorSlot,
            int light,
            A model,
            CallbackInfo ci) {
        if (!entity.isInvisible()) {
            return;
        }

        ItemStack armorStack = entity.getEquippedStack(armorSlot);
        if (armorStack.isEmpty()) {
            return;
        }

        if (armorStack.isOf(ModItems.NIGHTSTALKERS_MASK) ||
        armorStack.isOf(ModItems.NIGHTSTALKERS_JACKET) ||
        armorStack.isOf(ModItems.NIGHTSTALKERS_PANTS) ||
        armorStack.isOf(ModItems.NIGHTSTALKERS_STRIDES)) {
            ci.cancel();
        }
    }
}
