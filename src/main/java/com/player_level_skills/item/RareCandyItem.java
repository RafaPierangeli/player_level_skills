package com.player_level_skills.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.ServerPlayerSyncAccess;
import com.player_level_skills.init.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.Skill;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.item.consume.UseAction;
import net.minecraft.world.World;

public class RareCandyItem extends Item {

    public RareCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            int currentLevel = levelManager.getOverallLevel();
            int maxLevel = ConfigInit.CONFIG.overallMaxLevel;

            // 1. Lógica para quem ainda NÃO atingiu o nível máximo (Ganha 1 Nível)
            if (currentLevel < maxLevel) {
                // Sua lógica original de preencher a barra de XP para subir o nível
                ((ServerPlayerSyncAccess) player).addLevelExperience(
                        levelManager.getNextLevelExperience()
                                - ((int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience()))
                );
            }
            // 2. Lógica para quem JÁ está no nível máximo (Ganha 3 Pontos com Trava)
            else {
                int pointsToGive = 3;
                int currentPoints = levelManager.getSkillPoints();

                // Calcular quantos pontos faltam para maximizar TODAS as habilidades
                int neededPoints = 0;
                for (Skill skill : LevelManager.SKILLS.values()) {
                    int currentSkillLvl = levelManager.getSkillLevel(skill.getId());
                    if (currentSkillLvl < skill.getMaxLevel()) {
                        neededPoints += (skill.getMaxLevel() - currentSkillLvl);
                    }
                }

                // Verifica se o ganho de 3 pontos ultrapassa o que ele pode gastar
                if (currentPoints + pointsToGive > neededPoints) {
                    pointsToGive = Math.max(0, neededPoints - currentPoints);
                }

                if (pointsToGive > 0) {
                    levelManager.setSkillPoints(currentPoints + pointsToGive);
                    PacketHelper.updateLevels(player);

                    // Usando chave de tradução com argumento para o número de pontos
                    player.sendMessage(Text.translatable("text.levelz.rare_candy.bonus_points", pointsToGive).formatted(Formatting.GOLD), true);
                } else {
                    // Usando chave de tradução para quando não precisa de mais pontos
                    player.sendMessage(Text.translatable("text.levelz.rare_candy.maxed_skills").formatted(Formatting.RED), true);
                }
            }

            // Itens de consumo padrão
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            player.getHungerManager().add(2, 1.0f);
        }

        return stack;
    }


    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }
}
