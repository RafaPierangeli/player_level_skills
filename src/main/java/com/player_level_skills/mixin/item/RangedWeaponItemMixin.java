package com.player_level_skills.mixin.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponItemMixin {

    // para multishot
    @Inject(method = "shootAll", at = @At("HEAD"))
    private void player_level_skills$blockMultishot(
            ServerWorld world,
            LivingEntity shooter,
            net.minecraft.util.Hand hand,
            ItemStack stack,
            List<ItemStack> projectiles,
            float speed,
            float divergence,
            boolean critical,
            @Nullable LivingEntity target,
            CallbackInfo ci
    ) {

        // SE FOR CRIATIVO, NÃO BLOQUEIA
        if (shooter instanceof PlayerEntity player && player.isCreative()) return;

        if (!(shooter instanceof PlayerEntity player)) return;

        // Na 1.21.1, o multishot faz com que a lista 'projectiles' tenha 3 itens.
        // Se o jogador não tiver nível, limpamos as flechas extras da lista ANTES do disparo.
        if (projectiles.size() > 1) {
            RegistryEntry<Enchantment> multishotEntry = player.getEntityWorld().getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.MULTISHOT.getValue())
                    .orElse(null);

            if (multishotEntry != null) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                int level = EnchantmentHelper.getLevel(multishotEntry, stack);

                if (!levelManager.hasRequiredEnchantmentLevel(multishotEntry, level)) {
                    // Mantemos apenas a primeira flecha (a central)
                    ItemStack mainProjectile = projectiles.getFirst();
                    projectiles.clear();
                    projectiles.add(mainProjectile);

                    System.out.println("[DEBUG] Multishot bloqueado: disparando apenas 1 flecha.");
                }
            }
        }

    }
    //para infinity
        @Inject(method = "shootAll", at = @At("TAIL"))
        private void player_level_skills$forceConsumeIfInfinityBlocked(
                ServerWorld world,
                LivingEntity shooter,
                net.minecraft.util.Hand hand,
                ItemStack stack,
                List<ItemStack> projectiles,
                float speed,
                float divergence,
                boolean critical,
                net.minecraft.entity.LivingEntity target,
                CallbackInfo ci
        ) {
            if (!(shooter instanceof PlayerEntity player) || player.isCreative()) return;

            // 1. Pegamos o registro do Infinity
            RegistryEntry<Enchantment> infinityEntry = player.getEntityWorld().getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.INFINITY.getValue())
                    .orElse(null);

            if (infinityEntry != null) {
                int level = EnchantmentHelper.getLevel(infinityEntry, stack);

                // 2. Se o arco TEM Infinity, mas o jogador NÃO tem nível:
                if (level > 0) {
                    LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

                    if (!levelManager.hasRequiredEnchantmentLevel(infinityEntry, level)) {

                        // 3. Consumimos a flecha manualmente do inventário
                        // O metodo 'removeItem' procura o item de munição e diminui 1
                        for (int i = 0; i < player.getInventory().size(); i++) {
                            ItemStack invStack = player.getInventory().getStack(i);

                            // Verifica se é uma flecha comum (ajuste conforme necessário para flechas de efeitos)
                            if (!invStack.isEmpty() && invStack.getItem().equals(net.minecraft.item.Items.ARROW)) {
                                invStack.decrement(1);

                                // Se a pilha acabar, removemos o item
                                if (invStack.isEmpty()) {
                                    player.getInventory().removeStack(i);
                                }
                                break; // Removemos apenas UMA flecha por disparo
                            }
                        }
                    }
                }
            }
        }
    }
