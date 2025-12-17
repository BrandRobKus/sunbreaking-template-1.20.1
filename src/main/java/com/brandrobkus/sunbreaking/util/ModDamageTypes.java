package com.brandrobkus.sunbreaking.util;

import com.brandrobkus.sunbreaking.Sunbreaking;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> HAMMER_STRIKE =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Sunbreaking.MOD_ID, "hammer_strike"));

    public static final RegistryKey<DamageType> BULK_HAMMER_STRIKE =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Sunbreaking.MOD_ID, "bulk_hammer_strike"));

    public static void registerModDamageTypes() {
        System.out.println("Registered DamageTypes for " + Sunbreaking.MOD_ID);
    }
}
