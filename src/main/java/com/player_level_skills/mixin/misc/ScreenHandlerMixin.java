package com.player_level_skills.mixin.misc;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.RestrictionHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack cursorStack;

    @Shadow
    @Final
    @Mutable
    public DefaultedList<Slot> slots = DefaultedList.of();

//    @Unique
//    private PlayerEntity player_level_skills$currentPlayer;
//
//    @Inject(method = "onSlotClick", at = @At("HEAD"))
//    private void player_level_skills$capturePlayer(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
//        this.player_level_skills$currentPlayer = player;
//    }
//
//    @Inject(method = "canInsertIntoSlot(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;)Z", at = @At("HEAD"), cancellable = true)
//    private void player_level_skills$canInsertIntoSlot(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
//        if (stack.isEmpty() || slot == null) {
//            return;
//        }
//
//        if (!(slot.getClass().getName().startsWith("net.minecraft.screen.BrewingStandScreenHandler$"))) {
//            return;
//        }
//
//        PlayerEntity player = this.player_level_skills$currentPlayer;
//        if (player == null || player.isCreative()) {
//            return;
//        }
//
//        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//        if (!levelManager.hasRequiredCraftingLevel(stack.getItem())) {
//            cir.setReturnValue(false);
//        }
//    }

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void internalOnSlotClickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (player.isCreative()) {
            return;
        }
        if (slotIndex >= 0 && slotIndex != ScreenHandler.EMPTY_SPACE_SLOT_INDEX && RestrictionHelper.restrictSlotClick(player, actionType, this.cursorStack, this.slots.get(slotIndex), (ScreenHandler) (Object) this)) {
            info.cancel();
        }
    }

}
