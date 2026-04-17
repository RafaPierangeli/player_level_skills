package com.player_level_skills.mixin.item;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackServerMixin {

    @Inject(method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", at = @At(value = "HEAD"), cancellable = true)
    private void damageMixin(int amount, ServerWorld world, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo info) {
        if (BonusHelper.itemDamageChanceBonus(player)) {
            info.cancel();
        }
    }



    //para unbreaking no geral funciona 100%
    @Redirect(
            method = "calculateDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getItemDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;I)I")
    )
    private int player_level_skills$cancelUnbreakingBenefit(ServerWorld world, ItemStack stack, int amount, int originalAmount, ServerWorld worldParam, @Nullable ServerPlayerEntity player) {
        // 1. Se não houver jogador (ex: dano por máquina/dispenser) ou for criativo, segue o padrão
        if (player == null || player.isCreative()) {
            return EnchantmentHelper.getItemDamage(world, stack, amount);
        }

        // 2. Busca o registro do Unbreaking
        RegistryEntry<Enchantment> unbreakingEntry = world.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.UNBREAKING.getValue())
                .orElse(null);

        if (unbreakingEntry != null) {
            int level = EnchantmentHelper.getLevel(unbreakingEntry, stack);
            System.out.println("[DEBUG ItemStack] Bloqueando bônus de atributo de: " + unbreakingEntry.getIdAsString());

            // 3. Se o item TIVER Unbreaking, verificamos o nível do jogador
            if (level > 0) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

                // 4. Se o jogador NÃO tiver nível, retornamos o 'amount' original.
                // Isso faz com que o item sofra 100% do dano, ignorando a chance de defesa do Unbreaking.
                if (!levelManager.hasRequiredEnchantmentLevel(unbreakingEntry, level)) {
                    return amount;
                }
            }
        }

        // Caso tenha nível ou não tenha o encantamento, usa o cálculo padrão (com bônus)
        return EnchantmentHelper.getItemDamage(world, stack, amount);
    }
}
