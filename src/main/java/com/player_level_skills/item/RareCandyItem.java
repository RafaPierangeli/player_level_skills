package com.player_level_skills.item;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.ServerPlayerSyncAccess;
import com.player_level_skills.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

// Texture made by Pois1x
public class RareCandyItem extends Item {

    public RareCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient()) {
            if (!user.isCreative()) {
                stack.decrement(1);
            }
            LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();
            ((ServerPlayerSyncAccess) user)
                    .addLevelExperience(levelManager.getNextLevelExperience() - ((int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience())));
        }
        return ActionResult.SUCCESS;
    }

}
