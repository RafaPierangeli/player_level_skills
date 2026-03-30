package com.player_level_skills.mixin.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EquippableDispenserBehavior.class)
public class ArmorItemMixin {

    @Inject(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(armor.getItem())) {
                info.setReturnValue(false);
            }
        }
    }

}
