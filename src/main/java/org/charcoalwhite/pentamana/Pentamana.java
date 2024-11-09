package org.charcoalwhite.pentamana;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pentamana implements ModInitializer {
	public static final String MOD_ID = "pentamana";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String MANA = "pentamana.mana";
	public static final String MANA_CAPACITY = "pentamana.mana_capacity";
	public static final String MANA_REGEN = "pentamana.mana_regen";
	public static final String MANA_CONSUM = "pentamana.mana_consum";
	public static final String MANABAR_LIFE = "pentamana.manabar_life";
	public static final String CAPACITY = "pentamana:capacity";
	public static final String EMANATION = "pentamana:emanation";
	public static final String POTENCY = "pentamana:potency";
	public static final String UTILIZATION = "pentamana:utilization";

	// It is 2^24.
	public static final int MANA_SCALE = 16777216;
	
	// It is 2^24*2-1.
	public static final int MANA_CAPACITY_BASE = 33554431;

	// It is 2^24*2-1.
	public static final int MANA_CAPACITY_EXPANSION_BASE = 33554432;

	// It is 2^20.
	public static final int MANA_REGEN_BASE = 1048576;

	// It is 2^16
	public static final int MANA_REGEN_EXPANSION_BASE = 65536;

	public static final int MAX_MANABAR_LIFE = 40;
	public static final char MANA_STAR_INTACT = '\u2605';
	public static final char MANA_STAR_BROKEN = '\u2bea';
	public static final char MANA_STAR_HOLLOW = '\u2606';
	public static Scoreboard scoreboard;
	public static ScoreboardObjective manaObjective;
	public static ScoreboardObjective manaCapacityObjective;
	public static ScoreboardObjective manaRegenObjective;
	public static ScoreboardObjective manaConsumeObjective;
	public static ScoreboardObjective manabarLifeObjective;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[Pentamana] Loaded!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			getObjective(server);
		});
	}

	public static void getObjective(MinecraftServer server) {
		scoreboard = server.getScoreboard();
		manaObjective = scoreboard.getNullableObjective(MANA);
		if (manaObjective == null) {
			scoreboard.addObjective(MANA, ScoreboardCriterion.DUMMY, Text.of("Mana"), ScoreboardCriterion.RenderType.INTEGER, true, null);
			manaObjective = scoreboard.getNullableObjective(MANA);
		}

		manaCapacityObjective = scoreboard.getNullableObjective(MANA_CAPACITY);
		if (manaCapacityObjective == null) {
			scoreboard.addObjective(MANA_CAPACITY, ScoreboardCriterion.DUMMY, Text.of("Mana Capacity"), ScoreboardCriterion.RenderType.INTEGER, true, null);
			manaCapacityObjective = scoreboard.getNullableObjective(MANA_CAPACITY);
		}

		manaRegenObjective = scoreboard.getNullableObjective(MANA_REGEN);
		if (manaRegenObjective == null) {
			scoreboard.addObjective(MANA_REGEN, ScoreboardCriterion.DUMMY, Text.of("Mana Regen"), ScoreboardCriterion.RenderType.INTEGER, true, null);
			manaRegenObjective = scoreboard.getNullableObjective(MANA_REGEN);
		}

		manaConsumeObjective = scoreboard.getNullableObjective(MANA_CONSUM);
		if (manaConsumeObjective == null) {
			scoreboard.addObjective(MANA_CONSUM, ScoreboardCriterion.DUMMY, Text.of("Mana Consume"), ScoreboardCriterion.RenderType.INTEGER, true, null);
			manaConsumeObjective = scoreboard.getNullableObjective(MANA_CONSUM);
		}

		manabarLifeObjective = scoreboard.getNullableObjective(MANABAR_LIFE);
		if (manabarLifeObjective == null) {
			scoreboard.addObjective(MANABAR_LIFE, ScoreboardCriterion.DUMMY, Text.of("Manabar Life"), ScoreboardCriterion.RenderType.INTEGER, true, null);
			manabarLifeObjective = scoreboard.getNullableObjective(MANABAR_LIFE);
		}
	}
}
