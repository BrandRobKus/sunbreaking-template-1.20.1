package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.util.ServerScheduler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void sunbreakingTasks(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ServerScheduler.tick((MinecraftServer)(Object)this);
    }
}
