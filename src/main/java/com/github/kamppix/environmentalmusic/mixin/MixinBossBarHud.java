package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.access.IMixinBossBarHud;
import com.github.kamppix.environmentalmusic.access.IMixinMusicReplacer;
import com.github.kamppix.environmentalmusic.sound.ModMusicType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(BossBarHud.class)
public class MixinBossBarHud implements IMixinBossBarHud {
    @Shadow @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<UUID, ClientBossBar> bossBars;

    @Override
    public boolean shouldPlayRaidMusic() {
        for(Map.Entry<UUID, ClientBossBar> entry : bossBars.entrySet()) {
            ClientBossBar bar = entry.getValue();

            if (bar.getColor() == BossBar.Color.RED && bar.getName().getString().startsWith("Raid") && bar.getPercent() > 0.0f) {
                return true;
            }
        }
        return false;
    }
}
