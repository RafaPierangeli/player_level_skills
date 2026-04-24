package com.player_level_skills;

import com.player_level_skills.item.LootInjector;
import com.player_level_skills.item.TradeInjector;
import net.fabricmc.api.ModInitializer;
import com.player_level_skills.init.*;
import com.player_level_skills.network.LevelServerPacket;
import net.minecraft.util.Identifier;
import com.player_level_skills.init.ConfigInit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player_level_skills implements ModInitializer {
	public static final String MOD_ID = "player_level_skills";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CommandInit.init();
		//CompatInit.init();
		ConfigInit.init();
		CriteriaInit.init();
		EntityInit.init();
		EventInit.init();
		LoaderInit.init();
		LevelServerPacket.init();
		TagInit.init();
		ItemInit.registerModItems();
		RestrictionInit.init();
		TradeInjector.register();
		LootInjector.register();


	}
	public static Identifier identifierOf (String name) {
		return Identifier.of("player_level_skills", name);
	}
}