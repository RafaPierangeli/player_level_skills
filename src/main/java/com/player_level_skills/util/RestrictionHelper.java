package com.player_level_skills.util;

//import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
import net.fabricmc.loader.api.FabricLoader;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class RestrictionHelper {

    private static final boolean isAccessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");

    public static boolean restrictSlotClick(PlayerEntity playerEntity, SlotActionType actionType, ItemStack cursorStack, Slot slot, ScreenHandler screenHandler) {
        if (!playerEntity.isCreative()) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (actionType.equals(SlotActionType.QUICK_MOVE)) {
                if (screenHandler instanceof BrewingStandScreenHandler) {
                    return !slot.getStack().isEmpty() && !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                }
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
                if (screenHandler instanceof SmithingScreenHandler smithingScreenHandler) {
                    Slot outputSlot = smithingScreenHandler.getSlot(3);
                    if (slot == outputSlot) {
                        return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                    }
                }

                return !slot.getStack().isEmpty() && !levelManager.hasRequiredItemLevel(slot.getStack().getItem());


            }
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

            if (screenHandler instanceof SmithingScreenHandler smithingScreenHandler) {
                Slot outputSlot = smithingScreenHandler.getSlot(3);

                if (slot == outputSlot && !slot.getStack().isEmpty()) {
                    return !levelManager.hasRequiredCraftingLevel(slot.getStack().getItem());
                }
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
