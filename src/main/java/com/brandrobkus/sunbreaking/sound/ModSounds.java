package com.brandrobkus.sunbreaking.sound;
import com.brandrobkus.sunbreaking.Sunbreaking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent HAMMER_THROW = registerSoundEvent("hammer_throw");
    public static final SoundEvent BASE_HAMMER_THROW = registerSoundEvent("base_hammer_throw");
    public static final SoundEvent HAMMER_HIT = registerSoundEvent("hammer_hit");
    public static final SoundEvent HAMMER_RETURN = registerSoundEvent("hammer_return");
    public static final SoundEvent SHADOWSHOT_NODE = registerSoundEvent("shadowshot_node");
    public static final SoundEvent STORM_CLOUD = registerSoundEvent("storm_cloud");
    public static final SoundEvent STORM_BALL = registerSoundEvent("storm_ball");

    public static final SoundEvent COOLDOWN_END = registerSoundEvent("cooldown_end");
    public static final SoundEvent GEAR_COOLDOWN_END = registerSoundEvent("gear_cooldown_end");
    public static final SoundEvent INVISIBILITY_TRIGGER = registerSoundEvent("invisibility_trigger");
    public static final SoundEvent INVISIBILITY_END = registerSoundEvent("invisibility_end");
    public static final SoundEvent SMOKE_BOMB_THROW = registerSoundEvent("smoke_bomb_throw");
    public static final SoundEvent SMOKE_BOMB_EXPLODE = registerSoundEvent("smoke_bomb_explode");
    public static final SoundEvent GLAIVE_SHOT = registerSoundEvent("glaive_shot");
    public static final SoundEvent GLAIVE_BLOCK_START = registerSoundEvent("glaive_block_start");
    public static final SoundEvent GLAIVE_BLOCK_CONCURRENT = registerSoundEvent("glaive_block_concurrent");
    public static final SoundEvent GLAIVE_EXPLOSION = registerSoundEvent("glaive_explosion");
    public static final SoundEvent COOLDOWN_INDICATOR = registerSoundEvent("cooldown_indicator");
    public static final SoundEvent ASPECT_REMOVE = registerSoundEvent("aspect_remove");
    public static final SoundEvent ASPECT_EQUIP = registerSoundEvent("aspect_equip");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(Sunbreaking.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        Registry.register(Registries.SOUND_EVENT, id, soundEvent);
        Sunbreaking.LOGGER.info("Registered sound: " + id);
        return soundEvent;
    }



    public static void registerSounds() {
        Sunbreaking.LOGGER.info("Registering Sounds for " + Sunbreaking.MOD_ID);
    }
}

