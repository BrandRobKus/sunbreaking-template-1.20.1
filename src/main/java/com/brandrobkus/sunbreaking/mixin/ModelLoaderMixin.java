package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.Sunbreaking;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                          Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "hammer_of_sol_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addBlazingHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                                 Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "blazing_hammer_of_sol_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addHellbentHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                                 Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "hellbent_hammer_of_sol_3d", "inventory"));
    }


    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addWoodenHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "wooden_hammer_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addStoneHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "stone_hammer_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addIronHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "iron_hammer_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addGoldenHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "golden_hammer_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addDiamondHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "diamond_hammer_3d", "inventory"));
    }
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addNetheriteHammer(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "netherite_hammer_3d", "inventory"));
    }


    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V",
            ordinal = 3, shift = At.Shift.AFTER))
    public void addArcBond(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
                              Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(Sunbreaking.MOD_ID, "stormcallers_bond_3d", "inventory"));
    }
}