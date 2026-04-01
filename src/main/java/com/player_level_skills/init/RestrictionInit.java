package com.player_level_skills.init;

import com.player_level_skills.data.LevelDataLoader;
import com.player_level_skills.data.VRestrictionLoader;
import com.player_level_skills.util.PacketHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestrictionInit {

    public static final Logger LOGGER = LogManager.getLogger("LevelZ");


    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(LevelDataLoader.ID, LevelDataLoader::new);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (!success) {
                LOGGER.error("Failed to reload on {}", Thread.currentThread());
                return;
            }

//            RegistryWrapper.WrapperLookup wrapperLookup = server.getRegistryManager();
//            VRestrictionLoader restrictionLoader = new VRestrictionLoader(wrapperLookup);
//            restrictionLoader.reload(serverResourceManager);

            RegistryWrapper.WrapperLookup wrapperLookup = server.getRegistryManager();
            LevelDataLoader restrictionLoader = new LevelDataLoader(wrapperLookup);
            restrictionLoader.reload(serverResourceManager);

            for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                PacketHelper.updateSkills(serverPlayerEntity);
                PacketHelper.updatePlayerSkills(serverPlayerEntity, null);
            }

            LOGGER.info("Finished restriction reload on {}", Thread.currentThread());
        });

        LOGGER.info("RestrictionInit registered.");
    }
}