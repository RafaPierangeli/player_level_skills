package com.player_level_skills.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import com.player_level_skills.screen.SkillInfoScreen;
import com.player_level_skills.screen.SkillRestrictionScreen;
import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LevelzTab extends InventoryTab {

    public LevelzTab(Text title, Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public boolean canClick(Class<?> screenClass, MinecraftClient client) {
        if (screenClass.equals(SkillInfoScreen.class) || screenClass.equals(SkillRestrictionScreen.class)) {
            return true;
        }
        return super.canClick(screenClass, client);
    }

    @Override
    public void onClick(MinecraftClient client) {
        client.setScreen(new PlayerLevelSkillsScreen());
    }

}