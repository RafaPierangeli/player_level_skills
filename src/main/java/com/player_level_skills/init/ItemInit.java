package com.player_level_skills.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import com.player_level_skills.Player_level_skills;
import com.player_level_skills.item.RareCandyItem;
import com.player_level_skills.item.StrangePotionItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ItemInit {

    public static final Item STRANGE_POTION = registerItem("strange_potion", new StrangePotionItem(new Item.Settings().maxCount(16).registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of("player_level_skills", "strange_potion")))));

    public static final Item RARE_CANDY = registerItem("rare_candy", new StrangePotionItem(new Item.Settings().maxCount(16).registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of("player_level_skills", "rare_candy")))));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("player_level_skills", name), item);
    }

    public static void registerModItems() {

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            // Adiciona os itens utilitários normais
            entries.add(STRANGE_POTION);

        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            // Adiciona os itens utilitários normais
            entries.add(RARE_CANDY);

        });
    }

    public static void init() {
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.DRAGON_BREATH, Items.NETHER_STAR, STRANGE_POTION);
//        });
    }
}
