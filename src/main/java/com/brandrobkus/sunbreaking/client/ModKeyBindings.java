package com.brandrobkus.sunbreaking.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding TOGGLE_INVISIBILITY;

    public static void register() {
        TOGGLE_INVISIBILITY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.sunbreaking.toggle_invisibility",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.sunbreaking"
                )
        );
    }
}
