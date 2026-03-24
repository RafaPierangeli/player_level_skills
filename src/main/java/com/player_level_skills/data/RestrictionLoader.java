package com.player_level_skills.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.player_level_skills.Player_level_skills;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.Skill;
import com.player_level_skills.level.restriction.PlayerRestriction;
import com.player_level_skills.registry.EnchantmentRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public record RestrictionLoader(RegistryWrapper.WrapperLookup wrapperLookup) implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = Player_level_skills.identifierOf("restriction");
    private static final Logger LOGGER = LogManager.getLogger("LevelZ");

    private static final List<Integer> blockList = new ArrayList<>();
    private static final List<Integer> craftingList = new ArrayList<>();
    private static final List<Integer> entityList = new ArrayList<>();
    private static final List<Integer> itemList = new ArrayList<>();
    private static final List<Integer> miningList = new ArrayList<>();
    private static final List<Integer> enchantmentList = new ArrayList<>();

    /**
     * Cache cru das restrições. Aqui nós guardamos o JSON já parseado,
     * mas ainda sem converter skillKey -> skillId.
     */
    private static final List<PendingRestriction> PENDING_RESTRICTIONS = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        clearRuntimeState();
        PENDING_RESTRICTIONS.clear();

        if (!ConfigInit.CONFIG.restrictions) {
            return;
        }

        EnchantmentRegistry.updateEnchantments(this.wrapperLookup());

        manager.findResources("restriction", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                if (!ConfigInit.CONFIG.defaultRestrictions && id.getPath().endsWith("/default.json")) {
                    return;
                }

                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (String restrictionName : data.keySet()) {
                    JsonObject restrictionJsonObject = data.getAsJsonObject(restrictionName);

                    PendingRestriction pendingRestriction = parsePendingRestriction(restrictionName, restrictionJsonObject);
                    if (pendingRestriction != null) {
                        PENDING_RESTRICTIONS.add(pendingRestriction);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id, e.toString());
            }
        });
    }

    /**
     * Segunda fase: só chama depois que LevelManager.SKILLS já estiver pronto.
     */
    public static void applyPendingRestrictions() {
        clearRuntimeRestrictions();

        if (!ConfigInit.CONFIG.restrictions) {
            PENDING_RESTRICTIONS.clear();
            return;
        }

        if (LevelManager.SKILLS.isEmpty()) {
            LOGGER.warn("Restrictions not applied because skills are not loaded yet.");
            return;
        }

        Map<String, Integer> skillKeyIdMap = new HashMap<>();
        for (Skill skill : LevelManager.SKILLS.values()) {
            skillKeyIdMap.put(skill.getKey(), skill.getId());
        }

        for (PendingRestriction pending : PENDING_RESTRICTIONS) {
            Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();

            for (Map.Entry<String, Integer> entry : pending.skillRequirements.entrySet()) {
                Integer skillId = skillKeyIdMap.get(entry.getKey());
                if (skillId == null) {
                    LOGGER.warn("Restriction {} contains an unrecognized skill called {}.", pending.name, entry.getKey());
                    continue;
                }
                skillLevelRestrictions.put(skillId, entry.getValue());
            }

            if (skillLevelRestrictions.isEmpty()) {
                LOGGER.warn("Restriction {} does not contain any valid skills.", pending.name);
                continue;
            }

            boolean replace = pending.replace;

            // blocks
            for (String blockIdString : pending.blocks) {
                Identifier blockIdentifier = Identifier.of(blockIdString);
                if (Registries.BLOCK.containsId(blockIdentifier)) {
                    int blockRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(blockIdentifier));
                    if (blockList.contains(blockRawId)) {
                        continue;
                    }
                    if (replace) {
                        blockList.add(blockRawId);
                    }
                    LevelManager.BLOCK_RESTRICTIONS.put(blockRawId, new PlayerRestriction(blockRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized block id called {}.", pending.name, blockIdentifier);
                }
            }

            // crafting
            for (String craftingIdString : pending.crafting) {
                Identifier craftingIdentifier = Identifier.of(craftingIdString);
                if (Registries.ITEM.containsId(craftingIdentifier)) {
                    int craftingRawId = Registries.ITEM.getRawId(Registries.ITEM.get(craftingIdentifier));
                    if (craftingList.contains(craftingRawId)) {
                        continue;
                    }
                    if (replace) {
                        craftingList.add(craftingRawId);
                    }
                    LevelManager.CRAFTING_RESTRICTIONS.put(craftingRawId, new PlayerRestriction(craftingRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized crafting id called {}.", pending.name, craftingIdentifier);
                }
            }

            // entities
            for (String entityIdString : pending.entities) {
                Identifier entityIdentifier = Identifier.of(entityIdString);
                if (Registries.ENTITY_TYPE.containsId(entityIdentifier)) {
                    int entityRawId = Registries.ENTITY_TYPE.getRawId(Registries.ENTITY_TYPE.get(entityIdentifier));
                    if (entityList.contains(entityRawId)) {
                        continue;
                    }
                    if (replace) {
                        entityList.add(entityRawId);
                    }
                    LevelManager.ENTITY_RESTRICTIONS.put(entityRawId, new PlayerRestriction(entityRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized entity id called {}.", pending.name, entityIdentifier);
                }
            }

            // items
            for (String itemIdString : pending.items) {
                Identifier itemIdentifier = Identifier.of(itemIdString);
                if (Registries.ITEM.containsId(itemIdentifier)) {
                    int itemRawId = Registries.ITEM.getRawId(Registries.ITEM.get(itemIdentifier));
                    if (itemList.contains(itemRawId)) {
                        continue;
                    }
                    if (replace) {
                        itemList.add(itemRawId);
                    }
                    LevelManager.ITEM_RESTRICTIONS.put(itemRawId, new PlayerRestriction(itemRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized item id called {}.", pending.name, itemIdentifier);
                }
            }

            // mining
            for (String miningIdString : pending.mining) {
                Identifier miningIdentifier = Identifier.of(miningIdString);
                if (Registries.BLOCK.containsId(miningIdentifier)) {
                    int miningRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(miningIdentifier));
                    if (miningList.contains(miningRawId)) {
                        continue;
                    }
                    if (replace) {
                        miningList.add(miningRawId);
                    }
                    LevelManager.MINING_RESTRICTIONS.put(miningRawId, new PlayerRestriction(miningRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized mining id called {}.", pending.name, miningIdentifier);
                }
            }

            // enchantments
            for (Map.Entry<String, Integer> enchantmentEntry : pending.enchantments.entrySet()) {
                Identifier enchantmentIdentifier = Identifier.of(enchantmentEntry.getKey());
                int level = enchantmentEntry.getValue();

                if (EnchantmentRegistry.containsId(enchantmentIdentifier, level)) {
                    int enchantmentRawId = EnchantmentRegistry.getId(enchantmentIdentifier, level);
                    if (enchantmentList.contains(enchantmentRawId)) {
                        continue;
                    }
                    if (replace) {
                        enchantmentList.add(enchantmentRawId);
                    }
                    LevelManager.ENCHANTMENT_RESTRICTIONS.put(enchantmentRawId, new PlayerRestriction(enchantmentRawId, skillLevelRestrictions));
                } else {
                    LOGGER.warn("Restriction {} contains an unrecognized enchantment id called {}.", pending.name, enchantmentIdentifier);
                }
            }

            // components: mantido como placeholder, sem alterar sua lógica atual
            if (!pending.components.isEmpty()) {
                for (Map.Entry<String, String> componentEntry : pending.components.entrySet()) {
                    Identifier itemIdentifier = Identifier.of(componentEntry.getKey());
                    if (Registries.ITEM.containsId(itemIdentifier)) {
                        if (!Registries.DATA_COMPONENT_TYPE.containsId(Identifier.of(componentEntry.getValue()))) {
                            LOGGER.warn("Restriction {} contains an unrecognized component called {}.", pending.name, componentEntry.getValue());
                        }
                    } else {
                        LOGGER.warn("Restriction {} contains an unrecognized item id at component called {}.", pending.name, itemIdentifier);
                    }
                }
            }
        }

        LOGGER.info("Applied {} pending restrictions.", PENDING_RESTRICTIONS.size());
        PENDING_RESTRICTIONS.clear();
    }

    private static PendingRestriction parsePendingRestriction(String restrictionName, JsonObject restrictionJsonObject) {
        boolean replace = restrictionJsonObject.has("replace") && restrictionJsonObject.get("replace").getAsBoolean();

        Map<String, Integer> skillRequirements = new HashMap<>();
        if (restrictionJsonObject.has("skills") && restrictionJsonObject.get("skills").isJsonObject()) {
            JsonObject skillRestrictions = restrictionJsonObject.getAsJsonObject("skills");
            for (String skillKey : skillRestrictions.keySet()) {
                skillRequirements.put(skillKey, skillRestrictions.get(skillKey).getAsInt());
            }
        }

        List<String> blocks = readStringArray(restrictionJsonObject, "blocks");
        List<String> crafting = readStringArray(restrictionJsonObject, "crafting");
        List<String> entities = readStringArray(restrictionJsonObject, "entities");
        List<String> items = readStringArray(restrictionJsonObject, "items");
        List<String> mining = readStringArray(restrictionJsonObject, "mining");

        Map<String, Integer> enchantments = new HashMap<>();
        if (restrictionJsonObject.has("enchantments") && restrictionJsonObject.get("enchantments").isJsonObject()) {
            JsonObject enchantmentObject = restrictionJsonObject.getAsJsonObject("enchantments");
            for (String enchantment : enchantmentObject.keySet()) {
                enchantments.put(enchantment, enchantmentObject.get(enchantment).getAsInt());
            }
        }

        Map<String, String> components = new HashMap<>();
        if (restrictionJsonObject.has("components") && restrictionJsonObject.get("components").isJsonObject()) {
            JsonObject componentObject = restrictionJsonObject.getAsJsonObject("components");
            for (String component : componentObject.keySet()) {
                components.put(component, componentObject.get(component).getAsString());
            }
        }

        if (skillRequirements.isEmpty()) {
            LOGGER.warn("Restriction {} does not contain any valid skills.", restrictionName);
            return null;
        }

        return new PendingRestriction(
                restrictionName,
                replace,
                skillRequirements,
                blocks,
                crafting,
                entities,
                items,
                mining,
                enchantments,
                components
        );
    }

    private static List<String> readStringArray(JsonObject object, String key) {
        List<String> values = new ArrayList<>();
        if (object.has(key) && object.get(key).isJsonArray()) {
            for (JsonElement element : object.getAsJsonArray(key)) {
                values.add(element.getAsString());
            }
        }
        return values;
    }

    private static void clearRuntimeState() {
        clearRuntimeRestrictions();
        blockList.clear();
        craftingList.clear();
        entityList.clear();
        itemList.clear();
        miningList.clear();
        enchantmentList.clear();
    }

    private static void clearRuntimeRestrictions() {
        LevelManager.BLOCK_RESTRICTIONS.clear();
        LevelManager.CRAFTING_RESTRICTIONS.clear();
        LevelManager.ENTITY_RESTRICTIONS.clear();
        LevelManager.ITEM_RESTRICTIONS.clear();
        LevelManager.MINING_RESTRICTIONS.clear();
        LevelManager.ENCHANTMENT_RESTRICTIONS.clear();
    }

    private record PendingRestriction(
            String name,
            boolean replace,
            Map<String, Integer> skillRequirements,
            List<String> blocks,
            List<String> crafting,
            List<String> entities,
            List<String> items,
            List<String> mining,
            Map<String, Integer> enchantments,
            Map<String, String> components
    ) {
    }
}