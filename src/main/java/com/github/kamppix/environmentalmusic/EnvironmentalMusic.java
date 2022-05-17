package com.github.kamppix.environmentalmusic;

import com.github.kamppix.environmentalmusic.sound.ModMusicTypes;
import com.github.kamppix.environmentalmusic.sound.ModSounds;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentalMusic implements ClientModInitializer {
	public static final String MOD_ID = "environmentalmusic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() { ModSounds.registerModSounds(); }
}
