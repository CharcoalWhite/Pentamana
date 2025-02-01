package org.charcoalwhite.pentamana;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pentamana implements ModInitializer {
	public static final String MOD_ID = "pentamana";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// It is 2^24.
	public static final int MANA_SCALE = 16777216;
	
	// It is 2^24*2-1.
	public static final int MANA_CAPACITY_BASE = 33554431;

	// It is 2^24*2.
	public static final int MANA_CAPACITY_EXPANSION_BASE = 33554432;

	// It is 2^20.
	public static final int MANA_REGEN_BASE = 1048576;

	// It is 2^16
	public static final int MANA_REGEN_EXPANSION_BASE = 65536;

	public static final int MAX_MANABAR_LIFE = 40;
	public static final char MANA_CHAR_FULL = '\u2605';
	public static final char MANA_CHAR_HALF = '\u2bea';
	public static final char MANA_CHAR_ZERO = '\u2606';
	public static final Formatting MANA_COLOR = Formatting.AQUA;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[Pentamana] Loaded!");

	}
}
