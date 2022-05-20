package com.github.kamppix.environmentalmusic.mixin;

import com.github.kamppix.environmentalmusic.access.IMixinBossBarHud;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class MixinBossBarHud implements IMixinBossBarHud {
    @Shadow @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<UUID, ClientBossBar> bossBars;

    @Override
    public boolean shouldPlayRaidMusic() {
        for(Map.Entry<UUID, ClientBossBar> entry : bossBars.entrySet()) {
            ClientBossBar bar = entry.getValue();

            if (bar.getColor() == BossBar.Color.RED && bar.getName().getString().startsWith("Raid") && bar.getPercent() > 0.0F) {
                return true;
            }
        }
        return false;
    }
}
