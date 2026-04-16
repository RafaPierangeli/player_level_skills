package com.player_level_skills.level;

import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.level.restriction.PlayerRestriction;
import com.player_level_skills.registry.EnchantmentRegistry;
import com.player_level_skills.util.LevelHelper;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {


    public static final Map<Integer, Skill> SKILLS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> BLOCK_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> CRAFTING_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ENTITY_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ITEM_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> MINING_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ENCHANTMENT_RESTRICTIONS = new HashMap<>();
    public static final Map<String, SkillBonus> BONUSES = new HashMap<>();

    private final PlayerEntity playerEntity;
    private Map<Integer, PlayerSkill> playerSkills = new HashMap<>();

    // Level
    private int overallLevel;
    private int totalLevelExperience;
    private float levelProgress;
    private int skillPoints;

    public LevelManager(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;

        for (Skill skill : SKILLS.values()) {
            if (!this.playerSkills.containsKey(skill.getId())) {
                this.playerSkills.put(skill.getId(), new PlayerSkill(skill.getId(), 0));
            } else if (this.playerSkills.get(skill.getId()).getLevel() > skill.getMaxLevel()) {
                this.playerSkills.get(skill.getId()).setLevel(skill.getMaxLevel());
            }
        }
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void readNbt(ReadView view) {
        this.overallLevel = view.getInt("Level", 1);
        this.levelProgress = view.getFloat("LevelProgress", 0.0f);
        this.totalLevelExperience = view.getInt("TotalLevelExperience", 0);
        this.skillPoints = view.getInt("SkillPoints", 0);

        ReadView skillsView = view.getReadView("Skills");
        if (skillsView != null) {
            for (String key : skillsView.keys()) {
                ReadView skillView = skillsView.getReadView(key);
                if (skillView == null) {
                    continue;
                }

                PlayerSkill skill = PlayerSkill.readDataFromView(skillView);
                if (SKILLS.containsKey(skill.getId())) {
                    playerSkills.put(skill.getId(), skill);
                }
            }
        }
    }

    public void writeNbt(WriteView view) {
        view.putInt("Level", this.overallLevel);
        view.putFloat("LevelProgress", this.levelProgress);
        view.putInt("TotalLevelExperience", this.totalLevelExperience);
        view.putInt("SkillPoints", this.skillPoints);

        WriteView skillsView = view.get("Skills");
        for (Map.Entry<Integer, PlayerSkill> entry : playerSkills.entrySet()) {
            WriteView skillView = skillsView.get("Skill" + entry.getKey());
            entry.getValue().writeDataToNbt(skillView);
        }
    }


    public Map<Integer, PlayerSkill> getPlayerSkills() {
        return playerSkills;
    }

    public void setPlayerSkills(Map<Integer, PlayerSkill> playerSkills) {
        this.playerSkills = playerSkills;
    }

    public void setOverallLevel(int overallLevel) {
        this.overallLevel = overallLevel;
    }

    public int getOverallLevel() {
        return overallLevel;
    }

    public void setTotalLevelExperience(int totalLevelExperience) {
        this.totalLevelExperience = totalLevelExperience;
    }

    public int getTotalLevelExperience() {
        return totalLevelExperience;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setLevelProgress(float levelProgress) {
        this.levelProgress = levelProgress;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public void setSkillLevel(int skillId, int level) {
        this.playerSkills.get(skillId).setLevel(level);
    }

    // Na sua classe de utilitários ou LevelManager
    public static final ThreadLocal<ServerPlayerEntity> CURRENT_ATTACKER = new ThreadLocal<>();

    // Na sua classe de utilitários ou LevelManager
    public static final ThreadLocal<ServerPlayerEntity> CURRENT_MINER = new ThreadLocal<>();


    public int getSkillLevel(int skillId) {
        // Maybe add a containsKey check here
        return this.playerSkills.get(skillId).getLevel();
    }

    public void addExperienceLevels(int levels) {
        this.overallLevel += levels;
        this.skillPoints += ConfigInit.CONFIG.pointsPerLevel;
        if (this.overallLevel < 0) {
            this.overallLevel = 0;
            this.levelProgress = 0.0F;
            this.totalLevelExperience = 0;
        }
    }

    public boolean isMaxLevel() {
        if (ConfigInit.CONFIG.overallMaxLevel > 0) {
            return this.overallLevel >= ConfigInit.CONFIG.overallMaxLevel;
        } else {
            int maxLevel = 0;
            for (Skill skill : SKILLS.values()) {
                maxLevel += skill.getMaxLevel();
            }
            return this.overallLevel >= maxLevel;
        }
    }

    public boolean hasAvailableLevel() {
        return this.skillPoints > 0;
    }
    // Recommend to use https://www.geogebra.org/graphing
    public int getNextLevelExperience() {
        if (isMaxLevel()) {
            return 0;
        }
        int experienceCost = (int) (ConfigInit.CONFIG.xpBaseCost + ConfigInit.CONFIG.xpCostMultiplicator * Math.pow(this.overallLevel, ConfigInit.CONFIG.xpExponent));
        if (ConfigInit.CONFIG.xpMaxCost != 0) {
            return experienceCost >= ConfigInit.CONFIG.xpMaxCost ? ConfigInit.CONFIG.xpMaxCost : experienceCost;
        } else {
            return experienceCost;
        }
    }
    // block
    public boolean hasRequiredBlockLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredBlockLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }
    // crafting
    public boolean hasRequiredCraftingLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredCraftingLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // entity
    public boolean hasRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = Registries.ENTITY_TYPE.getRawId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = Registries.ENTITY_TYPE.getRawId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // item
    public boolean hasRequiredItemLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredItemLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // mining
    public boolean hasRequiredMiningLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredMiningLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // enchantment
    public boolean hasRequiredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, int level) {
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, int level) {
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    public boolean resetSkill(int skillId) {
        int level = this.getSkillLevel(skillId);
        if (level > 0) {
            this.setSkillPoints(this.getSkillPoints() + level);
            this.setSkillLevel(skillId, 0);
            PacketHelper.updatePlayerSkills((ServerPlayerEntity) this.playerEntity, null);
            LevelHelper.updateSkill((ServerPlayerEntity) this.playerEntity, SKILLS.get(skillId));
            PacketHelper.updateLevels((ServerPlayerEntity) this.playerEntity);
            return true;
        } else {
            return false;
        }
    }
}
