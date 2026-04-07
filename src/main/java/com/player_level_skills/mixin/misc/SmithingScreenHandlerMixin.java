package com.player_level_skills.mixin.misc;

import com.player_level_skills.access.LevelManagerAccess;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    @Nullable
    @Shadow
    private RecipeEntry<SmithingRecipe> currentRecipe;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

//    @Inject(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
//    private void updateResultMixin(CallbackInfo ci, SmithingRecipeInput smithingRecipeInput, List<RecipeEntry<SmithingRecipe>> list) {
//        if (!this.player.isCreative() && !((LevelManagerAccess) this.player).getLevelManager().hasRequiredCraftingLevel(list.get(0).value().craft(smithingRecipeInput, this.player.getEntityWorld().getRegistryManager()).getItem())) {
//            this.currentRecipe = null;
//            this.output.setStack(0, ItemStack.EMPTY);
//            ci.cancel();
//        }
//    }
}
