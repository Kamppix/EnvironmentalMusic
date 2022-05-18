package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import com.github.kamppix.environmentalmusic.sound.MusicSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(MusicTracker.class)
public class MusicPlayer {
    @Shadow
    private MinecraftClient client;
    private HashMap<MusicSound, MusicSoundInstance> playingMusic = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo info) {
        MusicSound currentType = this.client.getMusicType();

        if (!this.playingMusic.containsKey(currentType)) {
            this.play(currentType, info);
        }

        for (Iterator<Map.Entry<MusicSound, MusicSoundInstance>> itr = playingMusic.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<MusicSound, MusicSoundInstance> entry = itr.next();
            MusicSound type = entry.getKey();

            if (!this.client.getSoundManager().isPlaying(playingMusic.get(type))) {
                this.playingMusic.remove(type);
                continue;
            }

            if (type == currentType) {
                playingMusic.get(type).setVolume(Math.min(1.0f, playingMusic.get(type).getVolume() + 0.012375f));
            } else {
                playingMusic.get(type).setVolume(Math.max(0.01f, playingMusic.get(type).getVolume() - 0.012375f));
                if (playingMusic.get(type).getVolume() == 0.01f) {
                    this.client.getSoundManager().stop(playingMusic.get(type));
                    this.playingMusic.remove(type);
                }
            }
        }

        info.cancel();
    }

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void play(MusicSound type, CallbackInfo info) {
        MusicSoundInstance instance = new MusicSoundInstance(type);
        instance.getSoundSet(this.client.getSoundManager());

        if (instance.getSound() != SoundManager.MISSING_SOUND) {
            if (type != ModMusicTypes.MENU) instance.setVolume(0.01f);
            this.playingMusic.put(type, instance);
            this.client.getSoundManager().play(instance);
        }

        info.cancel();
    }

    @Inject(method = "stop", at = @At("HEAD"), cancellable = true)
    private void stop(CallbackInfo info) {
        for (Iterator<Map.Entry<MusicSound, MusicSoundInstance>> itr = playingMusic.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<MusicSound, MusicSoundInstance> entry = itr.next();
            MusicSound type = entry.getKey();
            MusicSoundInstance instance = entry.getValue();

            this.client.getSoundManager().stop(instance);
            this.playingMusic.remove(type);
        }

        info.cancel();
    }

    @Inject(method = "isPlayingType", at = @At("HEAD"), cancellable = true)
    private void isPlayingType(MusicSound query, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);

        this.playingMusic.forEach((type, instance) -> {
            if (query.getSound().getId().equals(instance.getId())) {
                info.setReturnValue(true);
            }
        });
    }
}
