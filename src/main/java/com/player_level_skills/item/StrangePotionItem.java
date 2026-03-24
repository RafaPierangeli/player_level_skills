package com.player_level_skills.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.LevelHelper;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrangePotionItem extends Item {

    public StrangePotionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof ServerPlayerEntity playerEntity) {
            Criteria.CONSUME_ITEM.trigger(playerEntity, stack);

            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            List<Integer> list = new ArrayList<>(levelManager.getPlayerSkills().keySet());
            Collections.shuffle(list);

            for (int skillId : list) {
                if (levelManager.resetSkill(skillId) && !ConfigInit.CONFIG.opStrangePotion) {
                    break;
                }
            }
            PacketHelper.updatePlayerSkills(playerEntity, null);

            if (!playerEntity.isCreative()) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                }
                playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }

            user.emitGameEvent(GameEvent.DRINK);
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

}
