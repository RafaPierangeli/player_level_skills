package com.player_level_skills.init;

import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//import com.player_level_skills.screen.LevelScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.FileWriter;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class KeyInit {
    public static KeyBinding screenKey = new KeyBinding("key.levelz.openskillscreen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, KeyBinding.Category.MISC);
    public static KeyBinding devKey = new KeyBinding("key.levelz.dev", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, KeyBinding.Category.MISC);

    public static void init() {
        // Registering
        KeyBindingHelper.registerKeyBinding(screenKey);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenKey.wasPressed()) {
                client.setScreen(new PlayerLevelSkillsScreen());
                return;
            }
        });
    }

    public static void writeId(String string) {
        try (FileWriter idFile = new FileWriter("idlist.json", true)) {
            idFile.append("\"" + string + "\",");
            idFile.append(System.lineSeparator());
        } catch (IOException ignored) {
        }
    }

}
