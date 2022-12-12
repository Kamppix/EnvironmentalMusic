package com.github.kamppix.environmentalmusic.sound;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;

public class ModMusicTypes {
    public static final MusicSound MENU = registerMusicSound(ModSoundEvents.MUSIC_MENU);
    public static final MusicSound OVERWORLD_DAY = registerMusicSound(ModSoundEvents.MUSIC_OVERWORLD_DAY);
    public static final MusicSound OVERWORLD_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_OVERWORLD_NIGHT);
    public static final MusicSound OCEAN_DAY = registerMusicSound(ModSoundEvents.MUSIC_OCEAN_DAY);
    public static final MusicSound OCEAN_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_OCEAN_NIGHT);
    public static final MusicSound JUNGLE_DAY = registerMusicSound(ModSoundEvents.MUSIC_JUNGLE_DAY);
    public static final MusicSound JUNGLE_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_JUNGLE_NIGHT);
    public static final MusicSound SKY_DAY = registerMusicSound(ModSoundEvents.MUSIC_SKY_DAY);
    public static final MusicSound SKY_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_SKY_NIGHT);
    public static final MusicSound UNDERGROUND = registerMusicSound(ModSoundEvents.MUSIC_UNDERGROUND);
    public static final MusicSound DESERT = registerMusicSound(ModSoundEvents.MUSIC_DESERT);
    public static final MusicSound SNOW = registerMusicSound(ModSoundEvents.MUSIC_SNOW);
    public static final MusicSound MUSHROOM_FIELDS = registerMusicSound(ModSoundEvents.MUSIC_MUSHROOM_FIELDS);
    public static final MusicSound LUSH_CAVES = registerMusicSound(ModSoundEvents.MUSIC_LUSH_CAVES);
    public static final MusicSound DEEP_DARK = registerMusicSound(ModSoundEvents.MUSIC_DEEP_DARK);
    public static final MusicSound RAIN_DAY = registerMusicSound(ModSoundEvents.MUSIC_RAIN_DAY);
    public static final MusicSound RAIN_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_RAIN_NIGHT);
    public static final MusicSound THUNDER = registerMusicSound(ModSoundEvents.MUSIC_THUNDER);
    public static final MusicSound OCEAN_MONUMENT = registerMusicSound(ModSoundEvents.MUSIC_OCEAN_MONUMENT);
    public static final MusicSound VILLAGE_DAY = registerMusicSound(ModSoundEvents.MUSIC_VILLAGE_DAY);
    public static final MusicSound VILLAGE_NIGHT = registerMusicSound(ModSoundEvents.MUSIC_VILLAGE_NIGHT);
    public static final MusicSound RAID = registerMusicSound(ModSoundEvents.MUSIC_RAID);
    public static final MusicSound NETHER = registerMusicSound(ModSoundEvents.MUSIC_NETHER);
    public static final MusicSound CRIMSON_FOREST = registerMusicSound(ModSoundEvents.MUSIC_CRIMSON_FOREST);
    public static final MusicSound WARPED_FOREST = registerMusicSound(ModSoundEvents.MUSIC_WARPED_FOREST);
    public static final MusicSound SOUL_SAND_VALLEY = registerMusicSound(ModSoundEvents.MUSIC_SOUL_SAND_VALLEY);
    public static final MusicSound WITHER = registerMusicSound(ModSoundEvents.MUSIC_WITHER);
    public static final MusicSound STRONGHOLD = registerMusicSound(ModSoundEvents.MUSIC_STRONGHOLD);
    public static final MusicSound THE_END = registerMusicSound(ModSoundEvents.MUSIC_THE_END);
    public static final MusicSound END_CITY = registerMusicSound(ModSoundEvents.MUSIC_END_CITY);
    public static final MusicSound ENDER_DRAGON = registerMusicSound(ModSoundEvents.MUSIC_ENDER_DRAGON);
    public static final MusicSound CREDITS = registerMusicSound(ModSoundEvents.MUSIC_CREDITS);

    public static final MusicSound NONE = registerMusicSound(null);

    private static MusicSound registerMusicSound(SoundEvent event) {
        return new MusicSound(event, 0, 0, false);
    }
}
