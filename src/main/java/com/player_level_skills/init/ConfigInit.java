package com.player_level_skills.init;

import com.player_level_skills.config.player_level_skillsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigInit {
    public static player_level_skillsConfig CONFIG = new player_level_skillsConfig();

    public static void init() {
        AutoConfig.register(player_level_skillsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(player_level_skillsConfig.class).getConfig();
    }

}
