package com.player_level_skills.client;

import com.player_level_skills.init.KeyInit;
import com.player_level_skills.init.RenderInit;
import com.player_level_skills.network.LevelClientPacket;
import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PlayerLevelSkillsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyInit.init();
        LevelClientPacket.init();
        RenderInit.init();
    }
}
