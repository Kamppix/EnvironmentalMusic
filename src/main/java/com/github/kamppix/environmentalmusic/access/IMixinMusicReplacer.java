package com.github.kamppix.environmentalmusic.access;

import net.minecraft.sound.MusicSound;

public interface IMixinMusicReplacer {
    MusicSound updateNoneMusicType(MusicSound type);

    MusicSound getMusicTypeDefault();
}
