package com.player_level_skills.client;

import com.player_level_skills.init.KeyInit;
import com.player_level_skills.init.RenderInit;
import com.player_level_skills.network.LevelClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerLevelSkillsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyInit.init();
        LevelClientPacket.init();
        RenderInit.init();
    }
}
