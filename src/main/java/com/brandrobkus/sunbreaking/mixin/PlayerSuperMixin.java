package com.brandrobkus.sunbreaking.mixin;

import com.brandrobkus.sunbreaking.util.gui.SunbreakingSuperComponent;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperHolder;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class PlayerSuperMixin implements PlayerSuperHolder {

    private final SunbreakingSuperComponent sunbreakingSuper = new SunbreakingSuperComponent();

    @Override
    public SunbreakingSuperComponent sunbreaking_getSuper() {
        return sunbreakingSuper;
    }
}
