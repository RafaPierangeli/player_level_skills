package com.player_level_skills.mixin.misc;

import com.player_level_skills.util.BonusHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    @Mutable
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

//    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
//    protected void canTakeOutputMixin(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> info) {
//        if (BonusHelper.anvilXpCapBonus(player)) {
//            info.setReturnValue(true);
//        }
//    }

    @Inject(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 4))
    private void updateResultMixin(CallbackInfo info) {

        ItemStack segundoSlot = this.input.getStack(1);
        boolean estaVazio = segundoSlot.isEmpty();

        if (this.levelCost.get() > 1) {

            this.levelCost.set(BonusHelper.anvilXpDiscountBonus(this.player, this.levelCost.get(),estaVazio));
        }
    }



    @ModifyConstant(method = "updateResult", constant = @Constant(intValue = 40))
    private int aumentarLimiteDeNivel(int constant) {
        if (BonusHelper.anvilXpCapBonus(this.player)) {
            return Integer.MAX_VALUE; // Agora o limite para esse jogador é nível 1000
        }
        return constant; // Para jogadores sem a skill, continua sendo 40
    }


    @Inject(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"), require = 0)
    private void onTakeOutputMixin(PlayerEntity playerEntity, ItemStack stack, CallbackInfo ci) {
        if (BonusHelper.anvilXpChanceBonus(playerEntity)) {
            this.levelCost.set(0);
        }
    }

    @Inject(method = "getLevelCost", at = @At(value = "HEAD"), cancellable = true)
    public void getLevelCostMixin(CallbackInfoReturnable<Integer> info) {

        ItemStack segundoSlot = this.input.getStack(1);
        boolean estaVazio = segundoSlot.isEmpty();
        int levelCost = BonusHelper.anvilXpDiscountBonus(this.player, this.levelCost.get(),estaVazio);
        if (levelCost != this.levelCost.get()) {
            info.setReturnValue(levelCost);
        }
    }
}