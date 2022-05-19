package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.access.IMixinBossBarHud;
import com.github.kamppix.environmentalmusic.access.IMixinMusicReplacer;
import com.github.kamppix.environmentalmusic.sound.ModMusicType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(MinecraftClient.class)
public class MixinMusicReplacer implements IMixinMusicReplacer {
    @Shadow
    private Screen currentScreen;
    @Shadow
    private ClientPlayerEntity player;
    @Shadow
    private InGameHud inGameHud;

    private static final int SPACE_LAYER = 200;
    private static final int UNDERGROUND_LAYER = 40;
    private static final int CAVERN_LAYER = -10;

    @Inject(method = "getMusicType()Lnet/minecraft/sound/MusicSound", at = @At("HEAD"), cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> info) {
        info.setReturnValue(getReplacedMusicType());
    }

    private MusicSound getReplacedMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return ModMusicType.CREDITS;
        }
        if (this.player != null) {
            MusicSound musicType = null;

            if (this.player.world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(this.player.getBlockPos().getX()), ChunkSectionPos.getSectionCoord(this.player.getBlockPos().getZ()))) {
                BlockPos playerPos = this.player.getBlockPos();
                RegistryEntry<Biome> biome = this.player.world.getBiome(playerPos);

                boolean isDay = isDayMusic();

                if (biome.matchesKey(BiomeKeys.RIVER) || biome.matchesKey(BiomeKeys.STONY_SHORE)) {
                    musicType = ModMusicType.NONE;
                } else if (biome.matchesKey(BiomeKeys.CRIMSON_FOREST)) {
                    musicType = ModMusicType.CRIMSON_FOREST;
                } else if (biome.matchesKey(BiomeKeys.WARPED_FOREST)) {
                    musicType = ModMusicType.WARPED_FOREST;
                } else if (biome.matchesKey(BiomeKeys.SOUL_SAND_VALLEY)) {
                    musicType = ModMusicType.SOUL_SAND_VALLEY;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_NETHER))) {
                    musicType = ModMusicType.NETHER;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_END))) {
                    musicType = ModMusicType.END;
                } else if (biome.matchesKey(BiomeKeys.BEACH) || (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_OCEAN)) && !biome.matchesKey(BiomeKeys.FROZEN_OCEAN) || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN))) {
                    musicType = isDay ? ModMusicType.OCEAN_DAY : ModMusicType.OCEAN_NIGHT;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE))) {
                    musicType = isDay ? ModMusicType.JUNGLE_DAY : ModMusicType.JUNGLE_NIGHT;
                } else if (biome.matchesKey(BiomeKeys.DESERT) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_SAVANNA)) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_BADLANDS))) {
                    musicType = ModMusicType.DESERT;
                } else if (biome.value().getTemperature() <= 0.0f || biome.matchesKey(BiomeKeys.SNOWY_BEACH) || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) {
                    musicType = ModMusicType.SNOW;
                } else if (biome.matchesKey(BiomeKeys.MUSHROOM_FIELDS)) {
                    musicType = ModMusicType.MUSHROOM;
                } else if (biome.matchesKey(BiomeKeys.LUSH_CAVES)) {
                    musicType = ModMusicType.LUSH_CAVES;
                } else if(biome.matchesKey(BiomeKeys.DEEP_DARK)) {
                    musicType = ModMusicType.DEEP_DARK;
                } else {
                    musicType = isDay ? ModMusicType.OVERWORLD_DAY : ModMusicType.OVERWORLD_NIGHT;
                }

                if (this.player.world.getDimensionKey() == DimensionTypes.OVERWORLD) {
                    int playerDepth = 0;
                    if (playerPos.getY() <= SPACE_LAYER) playerDepth++;
                    if (playerPos.getY() <= UNDERGROUND_LAYER) playerDepth++;
                    if (playerPos.getY() <= CAVERN_LAYER) playerDepth++;

                    switch (playerDepth) {
                        case 0:
                            musicType = isDay ? ModMusicType.SPACE_DAY : ModMusicType.SPACE_NIGHT;
                            break;
                        case 2:
                            if (biome.streamTags().noneMatch(Predicate.isEqual(BiomeTags.IS_OCEAN)) && musicType != ModMusicType.LUSH_CAVES && musicType != ModMusicType.DEEP_DARK) {
                                musicType = ModMusicType.UNDERGROUND;
                            }
                            break;
                        case 3:
                            if (musicType != ModMusicType.LUSH_CAVES && musicType != ModMusicType.DEEP_DARK) {
                                musicType = ModMusicType.UNDERGROUND;
                            }
                            break;
                    }

                    List<VillagerEntity> nearbyVillagers = this.player.world.getEntitiesByType(EntityType.VILLAGER, new Box(playerPos.subtract(new Vec3i(64, 64, 64)), playerPos.add(new Vec3i(64, 64, 64))), EntityPredicates.VALID_LIVING_ENTITY);
                    int villagersInRange = 0;
                    for (VillagerEntity villager : nearbyVillagers) {
                        if (this.player.distanceTo(villager) <= 48) {
                            villagersInRange++;
                        }
                    }
                    if (villagersInRange > 2 && musicType != ModMusicType.MUSHROOM) musicType = isDay ? ModMusicType.VILLAGE_DAY : ModMusicType.VILLAGE_NIGHT;

                    if (playerDepth == 1) {
                        if (this.player.world.isThundering() && (musicType == ModMusicType.NONE || musicType == ModMusicType.OVERWORLD_DAY || musicType == ModMusicType.OVERWORLD_NIGHT || musicType == ModMusicType.OCEAN_DAY || musicType == ModMusicType.OCEAN_NIGHT || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE)))) {
                            musicType = ModMusicType.THUNDER;
                        } else if (this.player.world.isRaining() && (musicType == ModMusicType.NONE || musicType == ModMusicType.OVERWORLD_DAY || musicType == ModMusicType.OVERWORLD_NIGHT)) {
                            musicType = isDay ? ModMusicType.RAIN_DAY : ModMusicType.RAIN_NIGHT;
                        }
                    }
                }

                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    musicType = ModMusicType.DRAGON;
                } else if (this.inGameHud.getBossBarHud().shouldDarkenSky()) {
                    musicType = ModMusicType.WITHER;
                } else if (((IMixinBossBarHud) (Object) this.inGameHud.getBossBarHud()).shouldPlayRaidMusic()) {
                    musicType = ModMusicType.RAID;
                }
            }

            return musicType;
        }
        return ModMusicType.MENU;
    }

    public boolean isDayMusic() {
        long daytime = this.player.world.getTimeOfDay();
        return daytime < 13050 || daytime >= 23450;
    }

    @Override
    public MusicSound updateNoneMusicType(MusicSound type) {
        boolean isDay = isDayMusic();
        if (type == ModMusicType.OVERWORLD_DAY || type == ModMusicType.OVERWORLD_NIGHT || type == ModMusicType.SPACE_DAY || type == ModMusicType.SPACE_NIGHT || type == ModMusicType.UNDERGROUND || type == ModMusicType.LUSH_CAVES || type == ModMusicType.DEEP_DARK || type == ModMusicType.RAIN_DAY || type == ModMusicType.RAIN_NIGHT || type == ModMusicType.THUNDER || type == ModMusicType.VILLAGE_DAY || type == ModMusicType.VILLAGE_NIGHT || type == ModMusicType.RAID || type == ModMusicType.WITHER) {
            return isDay ? ModMusicType.OVERWORLD_DAY : ModMusicType.OVERWORLD_NIGHT;
        }
        if (type == ModMusicType.OCEAN_DAY || type == ModMusicType.OCEAN_NIGHT) return isDay ? ModMusicType.OCEAN_DAY : ModMusicType.OCEAN_NIGHT;
        if (type == ModMusicType.JUNGLE_DAY || type == ModMusicType.JUNGLE_NIGHT) return isDay ? ModMusicType.JUNGLE_DAY : ModMusicType.JUNGLE_NIGHT;
        else return null;
    }

    @Override
    public MusicSound getMusicTypeDefault() {
        return isDayMusic() ? ModMusicType.OVERWORLD_DAY : ModMusicType.OVERWORLD_NIGHT;
    }
}
