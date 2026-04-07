package com.player_level_skills.util;

//import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.loader.api.FabricLoader;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class RestrictionHelper {

    private static final boolean isAccessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");

    private static boolean hasRestrictedEnchantment(LevelManager levelManager, ItemStack stack) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            if (!levelManager.hasRequiredEnchantmentLevel(entry.getKey(), entry.getIntValue())) {
                return true;
            }
        }

        return false;
    }

    public static boolean restrictSlotClick(PlayerEntity playerEntity, SlotActionType actionType, ItemStack cursorStack, Slot slot, ScreenHandler screenHandler) {
        if (!playerEntity.isCreative()) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (actionType.equals(SlotActionType.QUICK_MOVE)) {
                if (screenHandler instanceof BrewingStandScreenHandler) {
                    return !slot.getStack().isEmpty() && !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                }

                //Crafting - Quick
                if (screenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
                    Slot outputSlot = craftingScreenHandler.getOutputSlot();

                    // Bloqueia só a retirada do resultado, mas não o preview.
                    if (slot == outputSlot) {
                        ItemStack stack = slot.getStack();
                        if (!stack.isEmpty()) {
                            return !levelManager.hasRequiredCraftingLevel(stack.getItem());
                        }
                    }
                }
                //Anvil - Quick
                if (screenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
                    Slot outputSlot = anvilScreenHandler.getSlot(2);

                    if (slot == outputSlot) {
                        return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem()) || hasRestrictedEnchantment(levelManager, slot.getStack());
                    }
                    return false;
                }
                //Merchant - Quick
                if (screenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
                    Slot outputSlot = merchantScreenHandler.getSlot(2);

                    if (slot == outputSlot) {
                        return hasRestrictedEnchantment(levelManager, slot.getStack());
                    }

                    return false;
                }
                //Smithing - Quick
                if (screenHandler instanceof SmithingScreenHandler smithingScreenHandler) {
                    Slot outputSlot = smithingScreenHandler.getSlot(3);
                    if (slot == outputSlot) {
                        return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                    }
                }

                return !slot.getStack().isEmpty() && !levelManager.hasRequiredItemLevel(slot.getStack().getItem());


            }
            //Crafting mouse
            if (screenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
                Slot outputSlot = craftingScreenHandler.getOutputSlot();

                // Bloqueia só a retirada do resultado, mas não o preview.
                if (slot == outputSlot) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        return !levelManager.hasRequiredCraftingLevel(stack.getItem());
                    }
                }
            }
            //Smithing mouse
            if (screenHandler instanceof SmithingScreenHandler smithingScreenHandler) {
                Slot outputSlot = smithingScreenHandler.getSlot(3);

                if (slot == outputSlot && !slot.getStack().isEmpty()) {
                    return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                }
            }
            //Anvil mouse
            if (screenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
                Slot outputSlot = anvilScreenHandler.getSlot(2);

                if (slot == outputSlot) {
                    return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem()) || hasRestrictedEnchantment(levelManager, slot.getStack());
                }

                return false;
            }
            //Merchant - mouse
            if (screenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
                Slot outputSlot = merchantScreenHandler.getSlot(2);

                if (slot == outputSlot) {
                    return hasRestrictedEnchantment(levelManager, slot.getStack());
                }

                return false;
            }


            else if (!cursorStack.isEmpty()) {
                boolean isNonNormalSlot = !slot.getClass().equals(Slot.class);

                if (!levelManager.hasRequiredItemLevel(cursorStack.getItem())) {
                    if (screenHandler instanceof PlayerScreenHandler) {
                        if (isNonNormalSlot) {
                            return true;
                        }
//                    } else if (isAccessoriesLoaded && screenHandler instanceof AccessoriesMenuBase) {
//                        if (isNonNormalSlot) {
//                            return true;
//                        }
                    }
                }
                if (!levelManager.hasRequiredCraftingLevel(cursorStack.getItem()) && isNonNormalSlot) {
                    return true;
                }
            }
        }

        return false;
    }
}
