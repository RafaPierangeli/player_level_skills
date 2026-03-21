package com.player_level_skills.util;

import com.player_level_skills.Player_level_skills;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.Skill;
import com.player_level_skills.level.SkillAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LevelHelper {

    public static void updateSkill(ServerPlayerEntity serverPlayerEntity, Skill skill) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        for (SkillAttribute skillAttribute : skill.getAttributes()) {
            if (serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()) != null) {
                if (skillAttribute.getBaseValue() > -9999.0f) {
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).setBaseValue(skillAttribute.getBaseValue());
                }
                Identifier identifier = Player_level_skills.identifierOf(skill.getKey());
                if (serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).hasModifier(identifier)) {
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).removeModifier(identifier);
                }
                serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).addTemporaryModifier(new EntityAttributeModifier(identifier, skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.getId()), skillAttribute.getOperation()));
            }
        }
    }
}
