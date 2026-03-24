package com.player_level_skills.init;

import com.player_level_skills.data.RestrictionLoader;
import com.player_level_skills.data.SkillLoader;
import com.player_level_skills.util.PacketHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoaderInit {

    public static final Logger LOGGER = LogManager.getLogger("LevelZ");

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new SkillLoader());

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(RestrictionLoader.ID, RestrictionLoader::new);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (!success) {
                LOGGER.error("Failed to reload on {}", Thread.currentThread());
                return;
            }

            // 1) Agora as skills já foram carregadas pelo SkillLoader
            // 2) Aplicamos as restrições que ficaram em cache no RestrictionLoader
            RestrictionLoader.applyPendingRestrictions();

            // 3) Sincroniza todos os jogadores online
            for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                PacketHelper.updateSkills(serverPlayerEntity);
                PacketHelper.updateRestrictions(serverPlayerEntity);
                PacketHelper.updatePlayerSkills(serverPlayerEntity, null);
                PacketHelper.updateLevels(serverPlayerEntity);
            }

            LOGGER.info("Finished reload on {}", Thread.currentThread());
        });
    }
}