package com.player_level_skills;

import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.init.CriteriaInit;
import com.player_level_skills.init.EntityInit;
import com.player_level_skills.init.EventInit;
import com.player_level_skills.init.LoaderInit;
import com.player_level_skills.network.LevelServerPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.tag.TagInit;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player_level_skills implements ModInitializer {
	public static final String MOD_ID = "player_level_skills";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//CommandInit.init();
		//CompatInit.init();
		ConfigInit.init();
		CriteriaInit.init();
		EntityInit.init();
		EventInit.init();
		LoaderInit.init();
		LevelServerPacket.init();
		//TagInit.init();
		//ItemInit.init();

		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier identifierOf (String name) {
		return Identifier.of("player_level_skills", name);
	}
}