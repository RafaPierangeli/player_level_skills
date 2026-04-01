package com.player_level_skills.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import com.player_level_skills.Player_level_skills;
import com.player_level_skills.item.RareCandyItem;
import com.player_level_skills.item.StrangePotionItem;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.player_level_skills.item.ModFoodComponent;

public class ItemInit {

    public static final Item STRANGE_POTION = registerItem("strange_potion", new StrangePotionItem(new Item.Settings().food(ModFoodComponent.STRANGE_POTION,ModFoodComponent.STRANGE_POTION_EFFECT).useCooldown(2.0f).maxCount(16).registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of("player_level_skills", "strange_potion")))));

    public static final Item RARE_CANDY = registerItem("rare_candy", new RareCandyItem(new Item.Settings().food(ModFoodComponent.RARE_CANDY,ModFoodComponent.RARE_CANDY_EFFECT).useCooldown(2).maxCount(16).registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of("player_level_skills", "rare_candy")))));

    public static final ItemGroup PLAYER_LEVEL_SKILLS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Player_level_skills.MOD_ID, "player_level_skills"),
            FabricItemGroup.builder().icon(() -> new ItemStack(RARE_CANDY))
                    .displayName(Text.translatable("itemgroup.player_level_skills"))
                    .entries((displayContext, entries) -> {
                        entries.add(STRANGE_POTION);
                        entries.add(RARE_CANDY);
                    }).build());


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("player_level_skills", name), item);
    }

    public static void registerModItems() {

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            // Adiciona os itens utilitários normais
            entries.add(STRANGE_POTION);

        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            // Adiciona os itens utilitários normais
            entries.add(RARE_CANDY);

        });
    }

//    public static void init() {
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.BLAZE_ROD, Items.COAL, Items.CACTUS);
//        });
//    }
}
