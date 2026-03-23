package com.player_level_skills.init;

import com.player_level_skills.Player_level_skills;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagInit {

    public static final TagKey<Item> RESTRICTED_FURNACE_EXPERIENCE_ITEMS = TagKey.of(RegistryKeys.ITEM, Player_level_skills.identifierOf("restricted_furnace_experience_items"));
    public static final TagKey<Block> RESTRICTED_ORE_EXPERIENCE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Player_level_skills.identifierOf("restricted_ore_experience_blocks"));

    public static void init() {
    }
}
