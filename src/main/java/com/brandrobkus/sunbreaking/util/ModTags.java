package com.brandrobkus.sunbreaking.util;

import com.brandrobkus.sunbreaking.Sunbreaking;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> HAMMER =
                createTag("hammer_of_sol");
        public static final TagKey<Item> HAMMER_BASE =
                createTag("basic_hammers");
        public static final TagKey<Item> BONDS =
                createTag("bonds");
        public static final TagKey<Item> BONDS_BASE =
                createTag("bonds_base");
        public static final TagKey<Item> ARROWS =
                createTag("arrows");
        public static final TagKey<Item> GLAIVES =
                createTag("glaives");
        public static final TagKey<Item> SHADOWSHOTS =
                createTag("shadowshots");

        public static final TagKey<Item> GEAR_METER =
                createTag("gear_meter");
        public static final TagKey<Item> SUPER_METER =
                createTag("super_meter");

        public static final TagKey<Item> SOLAR_ASPECTS =
                createTag("solar_aspects");
        public static final TagKey<Item> VOID_ASPECTS =
                createTag("void_aspects");
        public static final TagKey<Item> ARC_ASPECTS =
                createTag("arc_aspects");
        public static final TagKey<Item> SOLAR_FRAGMENTS =
                createTag("solar_fragments");
        public static final TagKey<Item> VOID_FRAGMENTS =
                createTag("void_fragments");
        public static final TagKey<Item> ARC_FRAGMENTS =
                createTag("arc_fragments");

        public static final TagKey<Item> SUNBREAKER_ARMOR =
                createTag("sunbreaker_armor");
        public static final TagKey<Item> NIGHTSTALKER_ARMOR =
                createTag("nightstalker_armor");
        public static final TagKey<Item> STORMCALLER_ARMOR =
                createTag("stormcaller_armor");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(Sunbreaking.MOD_ID, name));
        }

    }
}



