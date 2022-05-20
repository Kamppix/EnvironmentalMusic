package com.github.kamppix.environmentalmusic.sound;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;

public class MusicSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {
    public MusicSoundInstance(MusicSound sound) {
        super(sound != null ? sound.getSound().getId() : null, SoundCategory.MUSIC, 1.0f, 1.0f, SoundInstance.createRandom(), true, 0, AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void tick() {

    }
}
