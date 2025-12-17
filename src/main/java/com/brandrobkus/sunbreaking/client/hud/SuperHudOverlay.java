package com.brandrobkus.sunbreaking.client.hud;

import com.brandrobkus.sunbreaking.Sunbreaking;
import com.brandrobkus.sunbreaking.item.custom.ModArcArmorItem;
import com.brandrobkus.sunbreaking.item.custom.ModSolarArmorItem;
import com.brandrobkus.sunbreaking.item.custom.ModVoidArmorItem;
import com.brandrobkus.sunbreaking.item.weapons.*;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.brandrobkus.sunbreaking.util.gui.SunbreakingSuperComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SuperHudOverlay {

    private static final Identifier TEXTURE =
            new Identifier(Sunbreaking.MOD_ID, "textures/gui/sunbreaking_bars.png");

    public static void render(DrawContext context, MinecraftClient client) {
        if (client.player == null) return;

        boolean hasGearItem = false;
        boolean hasSuperItem = false;

        for (ItemStack stack : client.player.getInventory().main) {
            if (!stack.isEmpty()) {
                if (stack.isIn(ModTags.Items.GEAR_METER)) hasGearItem = true;
                if (stack.isIn(ModTags.Items.SUPER_METER)) hasSuperItem = true;
            }
        }

        if (!hasGearItem || !hasSuperItem) {
            for (int i = 0; i < 4; i++) {
                ItemStack armor = client.player.getInventory().getArmorStack(i);
                if (!armor.isEmpty()) {
                    if (armor.getItem() instanceof ModArcArmorItem ||
                            armor.getItem() instanceof ModSolarArmorItem ||
                            armor.getItem() instanceof ModVoidArmorItem) {
                        hasSuperItem = true;
                    }
                }
            }
        }

        if (!hasGearItem && !hasSuperItem) return;

        SunbreakingSuperComponent superComp = PlayerSuperAccessor.get(client.player);
        float superValue = superComp.getSuper();
        float gearValue  = superComp.getGear();

        float superPct = Math.max(0f, Math.min(superValue / 100f, 1f));
        float gearPct  = Math.max(0f, Math.min(gearValue / 100f, 1f));

        int superWidth = (int)(superPct * 81);
        int gearWidth  = (int)(gearPct * 81);

        int gearX = client.getWindow().getScaledWidth() / 2 + 10;
        int superX = client.getWindow().getScaledWidth() / 2 + 10;

        int gearY = client.getWindow().getScaledHeight() - 56;
        int superY = client.getWindow().getScaledHeight() - 62;

        if (hasGearItem) {
            context.drawTexture(TEXTURE, gearX, gearY, 101, 64, 81, 5);
            if (gearWidth > 0) context.drawTexture(TEXTURE, gearX, gearY, 101, 69, gearWidth, 5);
        }

        if (hasSuperItem) {
            context.drawTexture(TEXTURE, superX, superY, 0, 64, 81, 5);
            if (superWidth > 0) context.drawTexture(TEXTURE, superX, superY, 0, 69, superWidth, 5);
        }

        renderCostBars(context, client.player, superX, superY - 16, gearX, gearY - 16, superWidth, gearWidth);
    }

    private static final int BAR_WIDTH = 81;
    private static final int SUPER_COST_U = 0;
    private static final int GEAR_COST_U  = 101;
    private static final int BAR_HEIGHT = 5;

    private static void renderCostBars(DrawContext context, PlayerEntity player,
                                       int superX, int superY, int gearX, int gearY, int superWidth, int gearWidth) {

        ItemStack main = player.getMainHandStack();
        ItemStack off  = player.getOffHandStack();

        float superCost = 0f;
        float gearCost  = 0f;

        int costV = 0;

        float currentGear = PlayerSuperAccessor.get(player).getGear();
        float currentSuper = PlayerSuperAccessor.get(player).getSuper();

        if (main.getItem() instanceof GlaiveItem || off.getItem() instanceof GlaiveItem) {
            gearCost = 25f;
        }

        if (main.getItem() instanceof SmokeBombItem || off.getItem() instanceof SmokeBombItem) {
            gearCost = 12.5f;
        }

        if (main.getItem() instanceof SolHammerItem solHammer) {
            float cost = solHammer.getSuperCost(main);
            if (cost > superCost) {
                superCost = cost;
                costV = 53;
            }
        }
        if (off.getItem() instanceof SolHammerItem solHammer) {
            float cost = solHammer.getSuperCost(off);
            if (cost > superCost) {
                superCost = cost;
                costV = 53;
            }
        }

        if (main.getItem() instanceof ShadowshotBowItem shadowshotBowItem) {
            float cost = shadowshotBowItem.getVoidSuperCost(main);
            if (cost > superCost) {
                superCost = cost;
                costV = 21;
            }
        }
        if (off.getItem() instanceof ShadowshotBowItem shadowshotBowItem) {
            float cost = shadowshotBowItem.getVoidSuperCost(off);
            if (cost > superCost) {
                superCost = cost;
                costV = 21;
            }
        }

        if (main.getItem() instanceof BondItem bondItem) {
            float cost = bondItem.getArcSuperCost(main);
            if (cost > superCost) {
                superCost = cost;
                costV = 37;
            }
        }
        if (off.getItem() instanceof BondItem bondItem) {
            float cost = bondItem.getArcSuperCost(off);
            if (cost > superCost) {
                superCost = cost;
                costV = 37;
            }
        }

        int superCostWidth = (int)((superCost / 100f) * BAR_WIDTH);
        int gearCostWidth = (int)((gearCost / 100f) * BAR_WIDTH);

        int visibleSuperWidth = Math.min(superCostWidth, superWidth);
        int visibleGearWidth  = Math.min(gearCostWidth, gearWidth);

        if (superCost > currentSuper) visibleSuperWidth = superCostWidth;
        if (gearCost > currentGear) visibleGearWidth = gearCostWidth;

        if (superCost < superWidth + 5) {
            int costWidth = Math.round((superCost / 100f) * BAR_WIDTH);
            int drawWidth = Math.min(costWidth, BAR_WIDTH);

            int offset = Math.max(0, superWidth - drawWidth);

            context.drawTexture(TEXTURE, superX + offset, superY + 16,
                    SUPER_COST_U + offset, costV, drawWidth, BAR_HEIGHT);
        } else if (superCost >= superWidth + 5){
            context.drawTexture(TEXTURE, superX, superY+16, 0, costV - 5, visibleSuperWidth, 5);
            if (superWidth > 0) context.drawTexture(TEXTURE, superX, superY+16, 0, costV, superWidth, 5);
        }

        if (gearCost < gearWidth + 5) {
            int costWidth = Math.round((gearCost / 100f) * BAR_WIDTH);
            int drawWidth = Math.min(costWidth, BAR_WIDTH);

            int offset = Math.max(0, gearWidth - drawWidth);

            context.drawTexture(TEXTURE, gearX + offset, gearY + 16,
                    GEAR_COST_U + offset, 53, drawWidth, BAR_HEIGHT
            );
        } else if (gearCost >= gearWidth + 5){
            context.drawTexture(TEXTURE, gearX, gearY+16, 101, 48, visibleGearWidth, 5);
            if (gearWidth > 0) context.drawTexture(TEXTURE, gearX, gearY+16, 101, 53, gearWidth, 5);
        }
    }
}
