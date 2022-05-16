package com.github.kamppix.environmentalmusic;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentalMusic implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("environmentalmusic");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
