package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import com.github.kamppix.environmentalmusic.sound.MusicSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
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
    private final HashMap<MusicSound, MusicSoundInstance> playingMusic = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo info) {
        if (this.client.options.getSoundVolume(SoundCategory.MUSIC) == 0.0f) {
            stop(info);

        } else {
            MusicSound currentType = this.client.getMusicType();

            if (currentType != null) {
                boolean dipVolume = currentType == ModMusicTypes.WARDEN;

                if (currentType == ModMusicTypes.NONE || currentType == ModMusicTypes.WARDEN) {
                    if (this.playingMusic.isEmpty() || getMaxVolumeMusicType() == ModMusicTypes.RAIN_DAY || getMaxVolumeMusicType() == ModMusicTypes.RAIN_NIGHT || getMaxVolumeMusicType() == ModMusicTypes.THUNDER || getMaxVolumeMusicType() == ModMusicTypes.WITHER) {
                        currentType = null; //((IMusicReplacer) this.client).getMusicTypeDefault();
                    } else {
                        currentType = getMaxVolumeMusicType();
                    }
                }

                if (!this.playingMusic.containsKey(currentType)) {
                    this.play(currentType, info);
                }

                Iterator<Map.Entry<MusicSound, MusicSoundInstance>> itr = playingMusic.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<MusicSound, MusicSoundInstance> entry = itr.next();
                    MusicSound type = entry.getKey();
                    MusicSoundInstance instance = entry.getValue();

                    this.client.getSoundManager().resumeAll();

                    if (!this.client.getSoundManager().isPlaying(instance)) {
                        itr.remove();
                        continue;
                    }

                    if (type == currentType) {
                        if (dipVolume) {
                            instance.setVolume(Math.max(0.1f, instance.getVolume() - 0.012375f));
                        } else {
                            instance.setVolume(Math.min(1.0f, instance.getVolume() + 0.012375f));
                        }
                    } else {
                        instance.setVolume(Math.max(0.01f, instance.getVolume() - 0.012375f));
                        if (instance.getVolume() == 0.01f) {
                            this.client.getSoundManager().stop(instance);
                            itr.remove();
                        }
                    }
                }
            }
        }

        info.cancel();
    }

    private MusicSound getMaxVolumeMusicType() {
        Map.Entry<MusicSound, MusicSoundInstance> maxEntry = null;

        for (Map.Entry<MusicSound, MusicSoundInstance> entry : playingMusic.entrySet()) {
            if (maxEntry == null || entry.getValue().getVolume() > maxEntry.getValue().getVolume()) {
                maxEntry = entry;
            }
        }

        if (maxEntry != null) return maxEntry.getKey();
        else return null;
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
        Iterator<Map.Entry<MusicSound, MusicSoundInstance>> itr = playingMusic.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<MusicSound, MusicSoundInstance> entry = itr.next();
            MusicSoundInstance instance = entry.getValue();

            this.client.getSoundManager().stop(instance);
            itr.remove();
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
