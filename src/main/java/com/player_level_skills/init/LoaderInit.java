package com.player_level_skills.init;

import com.player_level_skills.data.SkillLoader;
import com.player_level_skills.data.VRestrictionLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoaderInit {

    public static final Logger LOGGER = LogManager.getLogger("LevelZ");

    public static void init() {
        //ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SkillLoader());
        //ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(VRestrictionLoader.ID, VRestrictionLoader::new);

        LOGGER.info("SkillLoader registered.");
    }
}