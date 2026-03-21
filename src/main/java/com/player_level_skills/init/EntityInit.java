package com.player_level_skills.init;

import net.fabricmc.loader.api.FabricLoader;
import com.player_level_skills.Player_level_skills;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class EntityInit {

    public static final boolean isRedstoneBitsLoaded = FabricLoader.getInstance().isModLoaded("redstonebits");

    public static final EntityType<LevelExperienceOrbEntity> LEVEL_EXPERIENCE_ORB = EntityType.Builder.<LevelExperienceOrbEntity>create(LevelExperienceOrbEntity::new, SpawnGroup.MISC)
            .dimensions(0.5F, 0.5F).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("player_level_skill", "level_experience_orb")));

    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, Player_level_skills.identifierOf("level_experience_orb"), LEVEL_EXPERIENCE_ORB);
    }

}
