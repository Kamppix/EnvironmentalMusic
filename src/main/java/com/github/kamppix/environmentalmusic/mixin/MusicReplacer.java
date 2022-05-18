package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MusicReplacer {
    @Inject(method = "getMusicType()Lnet/minecraft/sound/MusicSound;", at = @At("HEAD"), cancellable = true)
    private void replaceMusicType(CallbackInfoReturnable<MusicSound> info) {
        info.setReturnValue(getReplacedMusicType());
    }

    @Shadow
    private Screen currentScreen;
    @Shadow
    private ClientPlayerEntity player;
    @Shadow
    private InGameHud inGameHud;

    private static final int SPACE_LAYER = 200;
    private static final int UNDERGROUND_LAYER = 40;
    private static final int CAVERN_LAYER = -10;

    private MusicSound getReplacedMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return ModMusicTypes.CREDITS;
        }
        if (this.player != null) {
            MusicSound musicType = null;

            RegistryEntry<Biome> registryEntry = this.player.world.getBiome(this.player.getBlockPos());
            Biome.Category category = Biome.getCategory(registryEntry);

            long daytime = this.player.world.getTimeOfDay();
            boolean isDay = daytime < 13050 || daytime >= 23450;

            if (category == Biome.Category.NETHER) {
                musicType = ModMusicTypes.NETHER;
            } else if (category == Biome.Category.THEEND) {
                musicType = ModMusicTypes.END;
            } else if (registryEntry.matchesKey(BiomeKeys.BEACH) || (category == Biome.Category.OCEAN && !(registryEntry.matchesKey(BiomeKeys.FROZEN_OCEAN)) || registryEntry.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN))) {
                musicType = isDay ? ModMusicTypes.OCEAN_DAY : ModMusicTypes.OCEAN_NIGHT;
            } else if (category == Biome.Category.JUNGLE) {
                musicType = isDay ? ModMusicTypes.JUNGLE_DAY : ModMusicTypes.JUNGLE_NIGHT;
            } else if (category == Biome.Category.DESERT || category == Biome.Category.SAVANNA || category == Biome.Category.MESA) {
                musicType = ModMusicTypes.DESERT;
            } else if (registryEntry.value().getTemperature() <= 0.0f || registryEntry.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) {
                musicType = ModMusicTypes.SNOW;
            } else if (category == Biome.Category.MUSHROOM) {
                musicType = ModMusicTypes.MUSHROOM;
            } else if (registryEntry.matchesKey(BiomeKeys.LUSH_CAVES)) {
                musicType = ModMusicTypes.LUSH_CAVES;
            } else if (registryEntry.matchesKey(BiomeKeys.RIVER)) {
                musicType = ModMusicTypes.NONE;
            } else {
                musicType = isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
            }

            BlockPos playerPos = this.player.getBlockPos();
            int playerDepth = 0;
            if (playerPos.getY() < SPACE_LAYER) {
                playerDepth++;
            }
            if (playerPos.getY() < UNDERGROUND_LAYER) {
                playerDepth++;
            }
            if (playerPos.getY() < CAVERN_LAYER) {
                playerDepth++;
            }

            switch (playerDepth) {
                case 0:
                    musicType = isDay ? ModMusicTypes.SPACE_DAY : ModMusicTypes.SPACE_NIGHT;
                    break;
                case 1:
                    if (this.player.world.isThundering() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT || musicType == ModMusicTypes.OCEAN_DAY || musicType == ModMusicTypes.OCEAN_NIGHT || category == Biome.Category.JUNGLE)) {
                        musicType = ModMusicTypes.THUNDER;
                    } else if (this.player.world.isRaining() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT)) {
                        musicType = isDay ? ModMusicTypes.RAIN_DAY : ModMusicTypes.RAIN_NIGHT;
                    }
                    break;
                case 2:
                    if (category != Biome.Category.OCEAN && musicType != ModMusicTypes.LUSH_CAVES) {
                        musicType = ModMusicTypes.UNDERGROUND;
                    }
                    break;
                case 3:
                    if (musicType != ModMusicTypes.LUSH_CAVES) {
                        musicType = ModMusicTypes.CAVERN;
                    }
                    break;
            }

            // village, raid, mansion, monument, stronghold
            if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                musicType = ModMusicTypes.DRAGON;
            } else if (this.inGameHud.getBossBarHud().shouldDarkenSky()) {
                musicType = ModMusicTypes.WITHER;
            }

            return musicType;
        }
        return ModMusicTypes.MENU;
    }
}
