package com.github.kamppix.environmentalmusic;

import com.github.kamppix.environmentalmusic.sound.ModSoundEvents;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentalMusic implements ClientModInitializer {
	public static final String MOD_ID = "environmentalmusic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() { ModSoundEvents.registerModSounds(); }
}
