package com.player_level_skills.mixin.player;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void updateResultMixin(ScreenHandler handler, ServerWorld world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @org.jspecify.annotations.Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci, CraftingRecipeInput craftingRecipeInput, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
//        if (player.isCreative()) {
//            return;
//        }
//        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//        if (!levelManager.hasRequiredCraftingLevel(itemStack.getItem())) {
//            resultInventory.setStack(0, ItemStack.EMPTY);
//            handler.setReceivedStack(0, ItemStack.EMPTY);
//            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, ItemStack.EMPTY));
//            ci.cancel();
//        }
    }

}
