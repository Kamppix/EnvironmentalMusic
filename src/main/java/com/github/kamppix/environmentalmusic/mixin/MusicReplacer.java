package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(MinecraftClient.class)
public class MusicReplacer /*implements MusicReplacerAccess*/ {
    @Shadow
    private Screen currentScreen;
    @Shadow
    private ClientPlayerEntity player;
    @Shadow
    private InGameHud inGameHud;

    private static final int SPACE_LAYER = 200;
    private static final int UNDERGROUND_LAYER = 40;
    private static final int CAVERN_LAYER = -10;

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

                long daytime = this.player.world.getTimeOfDay();
                boolean isDay = daytime < 13050 || daytime >= 23450;

                /*
                ServerWorld serverWorld = MinecraftClient.getInstance()..getWorld(this.player.world.getRegistryKey());
                System.out.println(serverWorld);
                */

                if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_NETHER))) {
                    musicType = ModMusicTypes.NETHER;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_END))) {
                    musicType = ModMusicTypes.END;
                } else if (biome.matchesKey(BiomeKeys.BEACH) || (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_OCEAN)) && !biome.matchesKey(BiomeKeys.FROZEN_OCEAN) || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN))) {
                    musicType = isDay ? ModMusicTypes.OCEAN_DAY : ModMusicTypes.OCEAN_NIGHT;
                } else if (biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE))) {
                    musicType = isDay ? ModMusicTypes.JUNGLE_DAY : ModMusicTypes.JUNGLE_NIGHT;
                } else if (biome.matchesKey(BiomeKeys.DESERT) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_SAVANNA)) || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_BADLANDS))) {
                    musicType = ModMusicTypes.DESERT;
                } else if (biome.value().getTemperature() <= 0.0f || biome.matchesKey(BiomeKeys.DEEP_FROZEN_OCEAN)) {
                    musicType = ModMusicTypes.SNOW;
                } else if (biome.matchesKey(BiomeKeys.MUSHROOM_FIELDS)) {
                    musicType = ModMusicTypes.MUSHROOM;
                } else if (biome.matchesKey(BiomeKeys.LUSH_CAVES)) {
                    musicType = ModMusicTypes.LUSH_CAVES;
                } else if(biome.matchesKey(BiomeKeys.DEEP_DARK)) {
                    musicType = ModMusicTypes.DEEP_DARK;
                } else if (biome.matchesKey(BiomeKeys.RIVER)) {
                    musicType = ModMusicTypes.NONE;
                } else {
                    musicType = isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
                }

                int playerDepth = 0;
                if (playerPos.getY() <= SPACE_LAYER) playerDepth++;
                if (playerPos.getY() <= UNDERGROUND_LAYER) playerDepth++;
                if (playerPos.getY() <= CAVERN_LAYER) playerDepth++;

                switch (playerDepth) {
                    case 0:
                        musicType = isDay ? ModMusicTypes.SPACE_DAY : ModMusicTypes.SPACE_NIGHT;
                        break;
                    case 1:
                        if (this.player.world.isThundering() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT || musicType == ModMusicTypes.OCEAN_DAY || musicType == ModMusicTypes.OCEAN_NIGHT || biome.streamTags().anyMatch(Predicate.isEqual(BiomeTags.IS_JUNGLE)))) {
                            musicType = ModMusicTypes.THUNDER;
                        } else if (this.player.world.isRaining() && (musicType == ModMusicTypes.NONE || musicType == ModMusicTypes.OVERWORLD_DAY || musicType == ModMusicTypes.OVERWORLD_NIGHT)) {
                            musicType = isDay ? ModMusicTypes.RAIN_DAY : ModMusicTypes.RAIN_NIGHT;
                        }
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

                List<VillagerEntity> nearbyVillagers = this.player.world.getEntitiesByType(EntityType.VILLAGER, new Box(playerPos.subtract(new Vec3i(64, 64, 64)), playerPos.add(new Vec3i(64, 64, 64))), EntityPredicates.VALID_LIVING_ENTITY);
                for (VillagerEntity villager : nearbyVillagers) {
                    System.out.println(villager.getBrain().getMemory(MemoryModuleType.HOME));
                    if (villager.getBrain().hasMemoryModule(MemoryModuleType.HOME) && this.player.distanceTo(villager) <= 64) {
                        musicType = isDay ? ModMusicTypes.VILLAGE_DAY : ModMusicTypes.VILLAGE_NIGHT;
                        break;
                    }
                }

                /*
                ServerWorld serverWorld = Objects.requireNonNull(this.player.getServer()).getWorld(this.player.world.getRegistryKey());
                System.out.println(serverWorld != null);
                if (serverWorld != null && serverWorld.hasRaidAt(playerPos)) {
                    System.out.println("RAID ACTIVE");
                    musicType = ModMusicTypes.RAID;
                }
                */

                // mansion, monument, stronghold
                List<WardenEntity> nearbyWardens = this.player.world.getEntitiesByType(EntityType.WARDEN, new Box(playerPos.subtract(new Vec3i(64, 64, 64)), playerPos.add(new Vec3i(64, 64, 64))), EntityPredicates.VALID_LIVING_ENTITY);
                for (WardenEntity warden : nearbyWardens) {
                    if (this.player.distanceTo(warden) <= 64) {
                        musicType = ModMusicTypes.WARDEN;
                        break;
                    }
                }

                if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                    musicType = ModMusicTypes.DRAGON;
                } else if (this.inGameHud.getBossBarHud().shouldDarkenSky()) {
                    musicType = ModMusicTypes.WITHER;
                }
            }

            return musicType;
        }
        return ModMusicTypes.MENU;
    }

    public MusicSound getMusicTypeDefault() {
        long daytime = this.player.world.getTimeOfDay();
        boolean isDay = daytime < 13050 || daytime >= 23450;

        return isDay ? ModMusicTypes.OVERWORLD_DAY : ModMusicTypes.OVERWORLD_NIGHT;
    }
}
