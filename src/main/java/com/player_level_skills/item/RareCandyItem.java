package com.player_level_skills.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.ServerPlayerSyncAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.item.consume.UseAction;
import net.minecraft.world.World;

public class RareCandyItem extends Item {

    public RareCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();

            ((ServerPlayerSyncAccess) player).addLevelExperience(
                    levelManager.getNextLevelExperience()
                            - ((int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience()))
            );

            if (!player.isCreative()) {
                stack.decrement(1);
            }
            player.getHungerManager().add(2, 0.0f);
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
