package com.player_level_skills.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnchantmentRegistry {

    private static final Logger LOGGER = LogManager.getLogger("PlayerLevelSkills");

    public static final Map<Integer, EnchantmentZ> ENCHANTMENTS = new HashMap<>();
    public static final Map<String, Integer> INDEX_ENCHANTMENTS = new HashMap<>();

    public static boolean containsId(RegistryEntry<Enchantment> enchantment, int level) {
        return containsId(enchantment.toString(), level);
    }

    public static boolean containsId(Identifier identifier, int level) {
        return containsId(identifier.toString(), level);
    }

    public static boolean containsId(String enchantment, int level) {
        return INDEX_ENCHANTMENTS.containsKey(enchantment + level);
    }

    public static EnchantmentZ getEnchantmentZ(int key) {
        return ENCHANTMENTS.get(key);
    }

    public static int getId(RegistryEntry<Enchantment> enchantment, int level) {
        return getId(enchantment.getIdAsString(), level);
    }

    public static int getId(Identifier identifier, int level) {
        return getId(identifier.toString(), level);
    }

    public static int getId(String enchantment, int level) {
        return getId(enchantment + level);
    }

    private static int getId(String enchantment) {
        if (INDEX_ENCHANTMENTS.containsKey(enchantment)) {
            return INDEX_ENCHANTMENTS.get(enchantment);
        }
        return -1;
    }

    public static void updateEnchantments(RegistryWrapper.WrapperLookup wrapperLookup) {
        ENCHANTMENTS.clear();
        INDEX_ENCHANTMENTS.clear();

        Optional<RegistryWrapper.Impl<Enchantment>> wrapper =
                (Optional<RegistryWrapper.Impl<Enchantment>>) wrapperLookup.getOptional(RegistryKeys.ENCHANTMENT);

        for (RegistryWrapper.Impl<Enchantment> enchantmentImpl : wrapper.stream().toList()) {
            for (RegistryEntry.Reference<Enchantment> enchantment : enchantmentImpl.streamEntries().toList()) {
                String enchantmentId = enchantment.getIdAsString();
                int maxLevel = enchantment.value().getMaxLevel();

                LOGGER.info("Loaded enchantment: {} | maxLevel={}", enchantmentId, maxLevel);

                for (int i = 1; i <= maxLevel; i++) {
                    int index = ENCHANTMENTS.size();
                    String key = enchantmentId + i;

                    INDEX_ENCHANTMENTS.put(key, index);
                    ENCHANTMENTS.put(index, new EnchantmentZ(enchantment, i));

                    LOGGER.info(" - registered level {} as key={} index={}", i, key, index);
                }
            }
        }

        LOGGER.info("Finished loading enchantments. Total registered: {}", ENCHANTMENTS.size());
    }
}