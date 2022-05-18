package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

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
    @Shadow
    private MusicTracker musicTracker;

    private static final int SPACE_LAYER = 200;
    private static final int UNDERGROUND_LAYER = 40;
    private static final int CAVERN_LAYER = -10;

    private MusicSound getReplacedMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return ModMusicTypes.CREDITS;
        }
        if (this.player != null) {
            MusicSound musicType = null;

            if (this.player.world.getRegistryKey() == World.END) {
                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    musicType = ModMusicTypes.DRAGON;
                } else {
                    musicType = ModMusicTypes.END;
                }

            } else if (this.player.world.getRegistryKey() == World.NETHER) {
                musicType = ModMusicTypes.NETHER;

            } else {
                long daytime = this.player.world.getTimeOfDay();
                boolean isDay = daytime < 13050 || daytime >= 23450;

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

                RegistryEntry<Biome> registryEntry = this.player.world.getBiome(this.player.getBlockPos());
                Biome.Category category = Biome.getCategory(registryEntry);

                //'plains', 'windswept_hills', 'forest', 'taiga', 'swamp', 'birch_forest', 'dark_forest', 'old_growth_pine_taiga', 'windswept_forest', 'sunflower_plains', 'windswept_gravelly_hills', 'old_growth_birch_forest', 'old_growth_spruce_taiga', 'stony_peaks', 'meadow', 'grove'
                if ((category == Biome.Category.OCEAN && !(registryEntry.matchesKey(BiomeKeys.FROZEN_OCEAN)) || registryEntry.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) || registryEntry.matchesKey(BiomeKeys.BEACH)) {
                    musicType = isDay ? ModMusicTypes.OCEAN_DAY : ModMusicTypes.OCEAN_NIGHT;
                } else if (category == Biome.Category.JUNGLE) {
                    musicType = isDay ? ModMusicTypes.JUNGLE_DAY : ModMusicTypes.JUNGLE_NIGHT;
                } else if (category == Biome.Category.DESERT || category == Biome.Category.SAVANNA || category == Biome.Category.MESA) {
                    musicType = ModMusicTypes.DESERT;
                } else if (category == Biome.Category.ICY || registryEntry.matchesKey(BiomeKeys.SNOWY_TAIGA) || registryEntry.matchesKey(BiomeKeys.SNOWY_BEACH) || registryEntry.matchesKey(BiomeKeys.FROZEN_RIVER) || registryEntry.matchesKey(BiomeKeys.FROZEN_OCEAN) || registryEntry.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN) || registryEntry.matchesKey(BiomeKeys.FROZEN_PEAKS) || registryEntry.matchesKey(BiomeKeys.JAGGED_PEAKS) || registryEntry.matchesKey(BiomeKeys.SNOWY_SLOPES)) {
                    musicType = ModMusicTypes.SNOW;
                } else if (category == Biome.Category.MUSHROOM) {
                    musicType = ModMusicTypes.MUSHROOM;
                } else if (registryEntry.matchesKey(BiomeKeys.LUSH_CAVES)) {
                    musicType = ModMusicTypes.LUSH_CAVES;
                }

                switch (playerDepth) {
                    case 0:
                        musicType = isDay ? ModMusicTypes.SPACE_DAY : ModMusicTypes.SPACE_NIGHT;
                        break;
                    case 1:
                        if (this.player.world.isThundering() && (musicType == null || musicType == ModMusicTypes.OCEAN_DAY || musicType == ModMusicTypes.OCEAN_NIGHT || category == Biome.Category.JUNGLE)) {
                            musicType = ModMusicTypes.THUNDER;
                        } else if (this.player.world.isRaining() && musicType == null) {
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

                // village, raid, mansion, monument, stronghold, wither

                if (musicType == null) {
                    musicType = isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
                }
            }

            return musicType;
        }
        return ModMusicTypes.MENU;
    }
}
