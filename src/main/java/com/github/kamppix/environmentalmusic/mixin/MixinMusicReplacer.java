package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.access.IMixinBossBarHud;
import com.github.kamppix.environmentalmusic.access.IMixinMusicReplacer;
import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.ShulkerEntity;
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

    private static final int SKY_LAYER = 200;
    private static final int UNDERGROUND_LAYER = 40;
    private static final int CAVERN_LAYER = -10;

    @SuppressWarnings("all")
    @Inject(method = "getMusicType()Lnet/minecraft/sound/MusicSound;", at = @At("HEAD"), cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> info) {
        info.setReturnValue(getReplacedMusicType());
    }

    private MusicSound getReplacedMusicType() {
        if (this.currentScreen instanceof CreditsScreen) {
            return ModMusicTypes.CREDITS;
        }
        if (this.player != null) {
            MusicSound musicType = null;

            if (this.player.world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(this.player.getBlockPos().getX()), ChunkSectionPos.getSectionCoord(this.player.getBlockPos().getZ()))) {
                BlockPos playerPos = this.player.getBlockPos();
                RegistryEntry<Biome> biome = this.player.world.getBiome(playerPos);

                boolean isDay = isDayMusic();

                if (biome.matchesKey(BiomeKeys.RIVER) || biome.matchesKey(BiomeKeys.STONY_SHORE)) {
                    musicType = ModMusicTypes.NONE;
                } else if (biome.matchesKey(BiomeKeys.CRIMSON_FOREST)) {
                    musicType = ModMusicTypes.CRIMSON_FOREST;
                } else if (biome.matchesKey(BiomeKeys.WARPED_FOREST)) {
                    musicType = ModMusicTypes.WARPED_FOREST;
                } else if (biome.matchesKey(BiomeKeys.SOUL_SAND_VALLEY)) {
                    musicType = ModMusicTypes.SOUL_SAND_VALLEY;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_NETHER))) {
                    musicType = ModMusicTypes.NETHER;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_END))) {
                    musicType = ModMusicTypes.THE_END;
                } else if (biome.matchesKey(BiomeKeys.BEACH) || (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_OCEAN)) && !biome.matchesKey(BiomeKeys.FROZEN_OCEAN) || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN))) {
                    musicType = isDay ? ModMusicTypes.OCEAN_DAY : ModMusicTypes.OCEAN_NIGHT;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE))) {
                    musicType = isDay ? ModMusicTypes.JUNGLE_DAY : ModMusicTypes.JUNGLE_NIGHT;
                } else if (biome.matchesKey(BiomeKeys.DESERT) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_SAVANNA)) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_BADLANDS))) {
                    musicType = ModMusicTypes.DESERT;
                } else if (biome.value().getTemperature() <= 0.0F || biome.matchesKey(BiomeKeys.SNOWY_BEACH) || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) {
                    musicType = ModMusicTypes.SNOW;
                } else if (biome.matchesKey(BiomeKeys.MUSHROOM_FIELDS)) {
                    musicType = ModMusicTypes.MUSHROOM_FIELDS;
                } else if (biome.matchesKey(BiomeKeys.LUSH_CAVES)) {
                    musicType = ModMusicTypes.LUSH_CAVES;
                } else if(biome.matchesKey(BiomeKeys.DEEP_DARK)) {
                    musicType = ModMusicTypes.DEEP_DARK;
                } else {
                    musicType = isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
                }

                if (this.player.world.getDimensionKey() == DimensionTypes.OVERWORLD) {
                    int playerDepth = 0;
                    if (playerPos.getY() <= SKY_LAYER) playerDepth++;
                    if (playerPos.getY() <= UNDERGROUND_LAYER) playerDepth++;
                    if (playerPos.getY() <= CAVERN_LAYER) playerDepth++;

                    switch (playerDepth) {
                        case 0:
                            musicType = isDay ? ModMusicTypes.SKY_DAY : ModMusicTypes.SKY_NIGHT;
                            break;
                        case 2:
                            if (biome.streamTags().noneMatch(Predicate.isEqual(BiomeTags.IS_OCEAN)) && musicType != ModMusicTypes.LUSH_CAVES && musicType != ModMusicTypes.DEEP_DARK) {
                                musicType = ModMusicTypes.UNDERGROUND;
                            }
                            break;
                        case 3:
                            if (musicType != ModMusicTypes.LUSH_CAVES && musicType != ModMusicTypes.DEEP_DARK) {
                                musicType = ModMusicTypes.UNDERGROUND;
                            }
                            break;
                    }

                    List<VillagerEntity> nearbyVillagers = this.player.world.getEntitiesByType(EntityType.VILLAGER, new Box(playerPos.subtract(new Vec3i(40, 40, 40)), playerPos.add(new Vec3i(40, 40, 40))), EntityPredicates.VALID_LIVING_ENTITY);
                    int villagersInRange = 0;
                    for (VillagerEntity villager : nearbyVillagers) {
                        if (this.player.distanceTo(villager) <= 40.0F) {
                            villagersInRange++;
                        }
                    }
                    if (villagersInRange > 2 && musicType != ModMusicTypes.MUSHROOM_FIELDS) musicType = isDay ? ModMusicTypes.VILLAGE_DAY : ModMusicTypes.VILLAGE_NIGHT;

                    List<GuardianEntity> nearbyGuardians = this.player.world.getEntitiesByType(EntityType.GUARDIAN, new Box(playerPos.subtract(new Vec3i(40, 40, 40)), playerPos.add(new Vec3i(40, 40, 40))), EntityPredicates.VALID_LIVING_ENTITY);
                    nearbyGuardians.addAll(this.player.world.getEntitiesByType(EntityType.ELDER_GUARDIAN, new Box(playerPos.subtract(new Vec3i(40, 40, 40)), playerPos.add(new Vec3i(40, 40, 40))), EntityPredicates.VALID_LIVING_ENTITY));
                    for (GuardianEntity guardian : nearbyGuardians) {
                        if (this.player.distanceTo(guardian) <= 40.0F) {
                            musicType = ModMusicTypes.OCEAN_MONUMENT;
                            break;
                        }
                    }

                    if (playerDepth == 1) {
                        if (this.player.world.isThundering() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT || musicType == ModMusicTypes.OCEAN_DAY || musicType == ModMusicTypes.OCEAN_NIGHT || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE)))) {
                            musicType = ModMusicTypes.THUNDER;
                        } else if (this.player.world.isRaining() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT)) {
                            musicType = isDay ? ModMusicTypes.RAIN_DAY : ModMusicTypes.RAIN_NIGHT;
                        }
                    }

                } else if (this.player.world.getDimensionKey() == DimensionTypes.THE_END) {
                    List<ShulkerEntity> nearbyShulkers = this.player.world.getEntitiesByType(EntityType.SHULKER, new Box(playerPos.subtract(new Vec3i(50, 50, 50)), playerPos.add(new Vec3i(50, 50, 50))), EntityPredicates.VALID_LIVING_ENTITY);
                    for (ShulkerEntity shulker : nearbyShulkers) {
                        if (this.player.distanceTo(shulker) <= 50.0F) {
                            musicType = ModMusicTypes.END_CITY;
                            break;
                        }
                    }
                }

                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    musicType = ModMusicTypes.ENDER_DRAGON;
                } else if (this.inGameHud.getBossBarHud().shouldDarkenSky()) {
                    musicType = ModMusicTypes.WITHER;
                } else if (((IMixinBossBarHud) (Object) this.inGameHud.getBossBarHud()).shouldPlayRaidMusic()) {
                    musicType = ModMusicTypes.RAID;
                }
            }

            return musicType;
        }
        return ModMusicTypes.MENU;
    }

    public boolean isDayMusic() {
        long daytime = this.player.world.getTimeOfDay() % 24000;
        return daytime < 13050 || daytime >= 23450;
    }

    @Override
    public MusicSound updateNoneMusicType(MusicSound type) {
        boolean isDay = isDayMusic();
        if (type == ModMusicTypes.OVERWORLD_DAY || type == ModMusicTypes.OVERWORLD_NIGHT || type == ModMusicTypes.SKY_DAY || type == ModMusicTypes.SKY_NIGHT || type == ModMusicTypes.UNDERGROUND || type == ModMusicTypes.LUSH_CAVES || type == ModMusicTypes.DEEP_DARK || type == ModMusicTypes.RAIN_DAY || type == ModMusicTypes.RAIN_NIGHT || type == ModMusicTypes.THUNDER || type == ModMusicTypes.VILLAGE_DAY || type == ModMusicTypes.VILLAGE_NIGHT || type == ModMusicTypes.RAID || type == ModMusicTypes.WITHER) {
            return isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
        }
        if (type == ModMusicTypes.OCEAN_DAY || type == ModMusicTypes.OCEAN_NIGHT) return isDay ? ModMusicTypes.OCEAN_DAY : ModMusicTypes.OCEAN_NIGHT;
        if (type == ModMusicTypes.JUNGLE_DAY || type == ModMusicTypes.JUNGLE_NIGHT) return isDay ? ModMusicTypes.JUNGLE_DAY : ModMusicTypes.JUNGLE_NIGHT;
        else return null;
    }

    @Override
    public MusicSound getMusicTypeDefault() {
        return isDayMusic() ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
    }
}
