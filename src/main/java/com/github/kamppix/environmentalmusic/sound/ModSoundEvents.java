package com.github.kamppix.environmentalmusic.sound;

import com.github.kamppix.environmentalmusic.EnvironmentalMusic;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents {
    public static final SoundEvent MUSIC_MENU = registerSoundEvent("music.menu");
    public static final SoundEvent MUSIC_OVERWORLD_DAY = registerSoundEvent("music.overworld_day");
    public static final SoundEvent MUSIC_OVERWORLD_NIGHT = registerSoundEvent("music.overworld_night");
    public static final SoundEvent MUSIC_OCEAN_DAY = registerSoundEvent("music.ocean_day");
    public static final SoundEvent MUSIC_OCEAN_NIGHT = registerSoundEvent("music.ocean_night");
    public static final SoundEvent MUSIC_JUNGLE_DAY = registerSoundEvent("music.jungle_day");
    public static final SoundEvent MUSIC_JUNGLE_NIGHT = registerSoundEvent("music.jungle_night");
    public static final SoundEvent MUSIC_SKY_DAY = registerSoundEvent("music.sky_day");
    public static final SoundEvent MUSIC_SKY_NIGHT = registerSoundEvent("music.sky_night");
    public static final SoundEvent MUSIC_UNDERGROUND = registerSoundEvent("music.underground");
    public static final SoundEvent MUSIC_DESERT = registerSoundEvent("music.desert");
    public static final SoundEvent MUSIC_SNOW = registerSoundEvent("music.snow");
    public static final SoundEvent MUSIC_MUSHROOM_FIELDS = registerSoundEvent("music.mushroom_fields");
    public static final SoundEvent MUSIC_LUSH_CAVES = registerSoundEvent("music.lush_caves");
    public static final SoundEvent MUSIC_DEEP_DARK = registerSoundEvent("music.deep_dark");
    public static final SoundEvent MUSIC_RAIN_DAY = registerSoundEvent("music.rain_day");
    public static final SoundEvent MUSIC_RAIN_NIGHT = registerSoundEvent("music.rain_night");
    public static final SoundEvent MUSIC_THUNDER = registerSoundEvent("music.thunder");
    public static final SoundEvent MUSIC_OCEAN_MONUMENT = registerSoundEvent("music.ocean_monument");
    public static final SoundEvent MUSIC_VILLAGE_DAY = registerSoundEvent("music.village_day");
    public static final SoundEvent MUSIC_VILLAGE_NIGHT = registerSoundEvent("music.village_night");
    public static final SoundEvent MUSIC_RAID = registerSoundEvent("music.raid");
    public static final SoundEvent MUSIC_NETHER = registerSoundEvent("music.nether");
    public static final SoundEvent MUSIC_CRIMSON_FOREST = registerSoundEvent("music.crimson_forest");
    public static final SoundEvent MUSIC_WARPED_FOREST = registerSoundEvent("music.warped_forest");
    public static final SoundEvent MUSIC_SOUL_SAND_VALLEY = registerSoundEvent("music.soul_sand_valley");
    public static final SoundEvent MUSIC_WITHER = registerSoundEvent("music.wither");
    public static final SoundEvent MUSIC_STRONGHOLD = registerSoundEvent("music.stronghold");
    public static final SoundEvent MUSIC_THE_END = registerSoundEvent("music.the_end");
    public static final SoundEvent MUSIC_END_CITY = registerSoundEvent("music.end_city");
    public static final SoundEvent MUSIC_ENDER_DRAGON = registerSoundEvent("music.ender_dragon");
    public static final SoundEvent MUSIC_CREDITS = registerSoundEvent("music.credits");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(EnvironmentalMusic.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerModSounds() {
        EnvironmentalMusic.LOGGER.info("Registering ModSoundEvents for " + EnvironmentalMusic.MOD_ID);
    }
}
