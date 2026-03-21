package com.player_level_skills.init;

import com.player_level_skills.config.ConfigInit;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.PlayerSkill;
import com.player_level_skills.level.Skill;
import com.player_level_skills.mixin.EntityAccessor;
import com.player_level_skills.util.LevelHelper;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Objects;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (Skill skill : LevelManager.SKILLS.values()) {
                LevelHelper.updateSkill(handler.getPlayer(), skill);
            }
            PacketHelper.syncEnchantments(handler.getPlayer());
            PacketHelper.updateSkills(handler.getPlayer());
            PacketHelper.updatePlayerSkills(handler.getPlayer(), null);
            PacketHelper.updateRestrictions(handler.getPlayer());
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            PacketHelper.updatePlayerSkills(player, null);
            PacketHelper.updateLevels(player);
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (alive) {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);
                PacketHelper.updateLevels(newPlayer);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            LevelManager newLevelManager = ((LevelManagerAccess) newPlayer).getLevelManager();

            if (ConfigInit.CONFIG.resetCurrentXp) {
                newLevelManager.setLevelProgress(0);
                newLevelManager.setTotalLevelExperience(0);
            }

            if (ConfigInit.CONFIG.levelRetainPercentage < 100) {
                LevelManager oldLevelManager = ((LevelManagerAccess) oldPlayer).getLevelManager();
                float levelRetainPercentageFloat = ConfigInit.CONFIG.levelRetainPercentage / 100;
                int retainedLevel = (int) (oldLevelManager.getOverallLevel() * levelRetainPercentageFloat);

                int pointsToDistribute = retainedLevel * ConfigInit.CONFIG.pointsPerLevel + ConfigInit.CONFIG.startPoints;

                if (oldLevelManager.getSkillPoints() > 0) {
                    int retainingSkillPoints = (int) (oldLevelManager.getSkillPoints() * levelRetainPercentageFloat);
                    newLevelManager.setSkillPoints(retainingSkillPoints);
                    pointsToDistribute -= retainingSkillPoints;
                }
                for (Map.Entry<Integer, PlayerSkill> entry : oldLevelManager.getPlayerSkills().entrySet()) {
                    int retainingLevel = (int) (entry.getValue().getLevel() * levelRetainPercentageFloat);
                    newLevelManager.setSkillLevel(entry.getKey(), retainingLevel);
                    pointsToDistribute -= retainingLevel;
                    if (pointsToDistribute < 0) {
                        break;
                    }
                }
                if (pointsToDistribute > 0) {
                    for (Map.Entry<Integer, PlayerSkill> entry : oldLevelManager.getPlayerSkills().entrySet()) {
                        if (entry.getValue().getLevel() < newLevelManager.getSkillLevel(entry.getKey())) {
                            int levelDifference = newLevelManager.getSkillLevel(entry.getKey()) - entry.getValue().getLevel();
                            if (levelDifference < pointsToDistribute) {
                                newLevelManager.setSkillLevel(entry.getKey(), newLevelManager.getSkillLevel(entry.getKey() + levelDifference));
                                pointsToDistribute -= levelDifference;
                            } else {
                                newLevelManager.setSkillLevel(entry.getKey(), newLevelManager.getSkillLevel(entry.getKey() + pointsToDistribute));
                                pointsToDistribute = 0;
                                break;
                            }
                        }
                    }
                    if (pointsToDistribute > 0) {
                        newLevelManager.setSkillPoints(newLevelManager.getSkillPoints() + pointsToDistribute);
                    }
                }

                PacketHelper.updatePlayerSkills(newPlayer, null);

                newLevelManager.setOverallLevel(retainedLevel);
                PacketHelper.updateLevels(newPlayer);
                newPlayer.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, newPlayer));
            } else {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);

                PacketHelper.updateLevels(newPlayer);
                for (Skill skill : LevelManager.SKILLS.values()) {
                    LevelHelper.updateSkill(newPlayer, skill);
                }
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredItemLevel(player.getStackInHand(hand).getItem())) {
                    // player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) +
                    // ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED), true);
                    player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                BlockPos blockPos = result.getBlockPos();
                if (world.canEntityModifyAt(player, blockPos)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                    if (!levelManager.hasRequiredBlockLevel(world.getBlockState(blockPos).getBlock())) {
                        player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                    if (!levelManager.hasRequiredEntityLevel(entity.getType())) {
                        player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

}
