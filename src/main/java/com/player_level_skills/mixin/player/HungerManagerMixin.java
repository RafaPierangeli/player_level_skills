package com.player_level_skills.mixin.player;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.SkillBonus;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;heal(F)V", ordinal = 1))
    private void updateStaminaMixin(ServerPlayerEntity player, CallbackInfo ci) {
        BonusHelper.healthRegenBonus(player);
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V", shift = Shift.AFTER, ordinal = 0))
    private void updateAbsorptionMixin(ServerPlayerEntity player, CallbackInfo ci) {
        BonusHelper.healthAbsorptionBonus(player);
    }



    @ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
    private FoodComponent player_level_skills$applyFoodBonus(FoodComponent food) {
        // Na 1.21.1, precisamos recuperar o player dono deste HungerManager.
        // Se você não tem o player fácil aqui, use o CURRENT_ATTACKER do seu playerTick!
        ServerPlayerEntity player = LevelManager.CURRENT_ATTACKER.get();

        //if (player == null) return food;

        if (player != null && LevelManager.BONUSES.containsKey("foodIncreasion")) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("foodIncreasion");
            int level = levelManager.getPlayerSkills().get(skillBonus.getId()).getLevel();

            if (level >= skillBonus.getLevel()) {
                float multiplier = level * ConfigInit.CONFIG.foodIncreasionBonus;

                // 2. Lógica da Saturação (Chance de bônus fixo +1.0)
                float currentSaturation = food.saturation();

                // Ex: Nível 20 * 0.025 = 0.5 (50% de chance)
                float chance = level * ConfigInit.CONFIG.foodSaturationChanceBonus;

                if (player.getRandom().nextFloat() <= chance) {
                    // Se ganhar na sorte, soma +1.0 na saturação original
                    currentSaturation += 1.0f;
                    System.out.println("[DEBUG] Bônus de Saturação Ativado!" + currentSaturation);
                }

                // Criamos um novo Record de FoodComponent com os valores multiplicados
                // Na Build 4: food.nutrition() e food.saturation()
                System.out.println("[DEBUG eat] Bônus aplicado para: " + player.getName().getString());
                return new FoodComponent((int) (food.nutrition() * multiplier), currentSaturation, food.canAlwaysEat());
            }
        }
        System.out.println("[DEBUG eat] Player info: !");
        return food;
    }

}