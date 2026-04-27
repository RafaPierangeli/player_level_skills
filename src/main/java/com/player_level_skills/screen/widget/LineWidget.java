package com.player_level_skills.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.player_level_skills.Player_level_skills;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.restriction.PlayerRestriction;
import com.player_level_skills.mixin.entity.VehicleEntityAccessor;
import com.player_level_skills.registry.EnchantmentRegistry;
import com.player_level_skills.registry.EnchantmentZ;
import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;

@Environment(EnvType.CLIENT)
public class LineWidget {

    private final MinecraftClient client;
    @Nullable
    private final Text text;
    @Nullable
    private final Map<Integer, PlayerRestriction> restrictions;
    private final int code;

    private Map<Integer, ItemStack> customStacks;
    private Map<Integer, Identifier> customImages;

    /**
     * @param code 0 = item, 1 = block, 2 = entity, 3 = enchantment
     */
    public LineWidget(MinecraftClient client, @Nullable Text text, @Nullable Map<Integer, PlayerRestriction> restrictions, int code) {
        this.client = client;
        this.text = text;
        this.restrictions = restrictions;
        this.code = code;

        if (this.code == 2) {
            this.customStacks = new HashMap<>();
            this.customImages = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);
                boolean imageExists = false;
                try {
                    client.getResourceManager().getResourceOrThrow(Player_level_skills.identifierOf("textures/gui/sprites/entity/" + Registries.ENTITY_TYPE.getId(entityType).getPath() + ".png"));
                    imageExists = true;
                } catch (FileNotFoundException ignored) {
                }
                if (imageExists) {
                    this.customImages.put(id, Player_level_skills.identifierOf("textures/gui/sprites/entity/" + Registries.ENTITY_TYPE.getId(entityType).getPath() + ".png"));
                } else if (SpawnEggItem.forEntity(entityType) != null) {
                    this.customStacks.put(id, new ItemStack(Objects.requireNonNull(SpawnEggItem.forEntity(entityType))));
                } else {
                    this.customImages.put(id, Player_level_skills.identifierOf("textures/gui/sprites/entity/default.png"));
                }
            }
        }
        else if (this.code == 3) {
            this.customStacks = new HashMap<>();

            for (Integer id : this.restrictions.keySet()) {
                EnchantmentZ enchantmentZ = EnchantmentRegistry.getEnchantmentZ(id);

                ItemStack stack = new ItemStack(net.minecraft.item.Items.ENCHANTED_BOOK);
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                builder.add(enchantmentZ.getEntry(), enchantmentZ.getLevel());
                stack.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

                this.customStacks.put(id, stack);
            }
        }
        else if (this.code == 4) {
            this.customStacks = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                // Agora o ID é 24, 34, etc. O Minecraft vai encontrar!
                net.minecraft.potion.Potion potion = Registries.POTION.get(id);

                if (potion != null) {
                    var entry = Registries.POTION.getEntry(potion);
                    // Cria a poção colorida e com nome
                    ItemStack stack = net.minecraft.component.type.PotionContentsComponent.createStack(
                            net.minecraft.item.Items.POTION,
                            entry
                    );
                    this.customStacks.put(id, stack);
                } else {
                    this.customStacks.put(id, new ItemStack(net.minecraft.item.Items.POTION));
                }
            }
        }


    }

    public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if (text != null) {
            drawContext.drawText(this.client.textRenderer, this.text, x, y + 4, 0xFF3F3F3F, false);
        } else {
            int separator = 0;
            boolean showTooltip = false;
            for (Map.Entry<Integer, PlayerRestriction> entry : this.restrictions.entrySet()) {
                Text tooltipTitle;
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,PlayerLevelSkillsScreen.ICON_TEXTURE, x + separator - 1, y - 1, 0, 148, 18, 18,256,256);
                if (this.code == 0) {
                    Item item = Registries.ITEM.get(entry.getKey());
                    tooltipTitle = item.getName();
                    drawContext.drawItem(Registries.ITEM.get(entry.getKey()).getDefaultStack(), x + separator, y);
                } else if (this.code == 1) {
                    Block block = Registries.BLOCK.get(entry.getKey());
                    tooltipTitle = block.getName();
                    drawContext.drawItem(block.asItem().getDefaultStack(), x + separator, y);
                } else if (this.code == 2) {
                    EntityType<?> entityType = Registries.ENTITY_TYPE.get(entry.getKey());
                    tooltipTitle = entityType.getName();
                    if (this.customStacks.containsKey(entry.getKey())) {
                        drawContext.drawItem(this.customStacks.get(entry.getKey()), x + separator, y);
                    } else {
                        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,this.customImages.get(entry.getKey()), x + separator, y, 0, 0, 16, 16,16,16);
                    }

                } else if (this.code == 3) {
                    ItemStack stack = this.customStacks.get(entry.getKey());
                    var enchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
                    // Pega o primeiro encantamento do livro para o título
                    RegistryEntry<Enchantment> enchantment = enchantments.getEnchantments().iterator().next();
                    int level = enchantments.getLevel(enchantment);
                    tooltipTitle = Enchantment.getName(enchantment, level);
                    drawContext.drawItem(stack, x + separator, y);
                }
                else if (this.code == 4) {
                    ItemStack stack = this.customStacks.get(entry.getKey());
                    var contents = stack.get(DataComponentTypes.POTION_CONTENTS);

                    if (contents != null && contents.potion().isPresent()) {
                        // Pega o ID da poção (ex: "healing", "strong_strength")
                        String potionPath = contents.potion().get().getKey().get().getValue().getPath();

                        // O segredo na 1.21.1: O nome da poção é composto pelo item + o efeito
                        // Ex: item.minecraft.potion.effect.healing
                        String translationKey = "item.minecraft.potion.effect." + potionPath;
                        MutableText translatedName = Text.translatable(translationKey);

                        // Se o nome vier vazio ou igual à chave (não traduzido), usa o fallback do item
                        if (translatedName.getString().equals(translationKey)) {
                            translatedName = (MutableText) stack.getName();
                        }

                        // Adiciona a distinção visual que fizemos
                        if (potionPath.contains("strong")) {
                            tooltipTitle = translatedName.append(Text.literal(" II").formatted(Formatting.YELLOW));
                        } else if (potionPath.contains("long")) {
                            tooltipTitle = translatedName.append(Text.literal(" long").formatted(Formatting.AQUA));
                        } else {
                            tooltipTitle = translatedName;
                        }
                    } else {
                        tooltipTitle = stack.getName();
                    }

                    drawContext.drawItem(stack, x + separator, y);
                }

                else {
                    tooltipTitle = Text.literal("");
                }

                if (!showTooltip && PlayerLevelSkillsScreen.isPointWithinBounds(x + separator, y, 16, 16, mouseX, mouseY)) {
                    List<Text> tooltip = new ArrayList<>();
                    tooltip.add(tooltipTitle);
                    for (Map.Entry<Integer, Integer> restriction : entry.getValue().getSkillLevelRestrictions().entrySet()) {
                        tooltip.add(Text.of(LevelManager.SKILLS.get(restriction.getKey()).getText().getString() + " " + Text.translatable("text.levelz.gui.short_level", restriction.getValue()).getString()));
                    }
                    drawContext.drawTooltip(this.client.textRenderer, tooltip, mouseX, mouseY);
                    showTooltip = true;
                }
                separator += 18;
            }
        }
    }
}

