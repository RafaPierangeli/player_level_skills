package com.player_level_skills.mixin.player;

import com.mojang.authlib.GameProfile;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.ServerPlayerSyncAccess;
import com.player_level_skills.init.CriteriaInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.PacketHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerSyncAccess {

    @Unique
    private final LevelManager levelManager = ((LevelManagerAccess) this).getLevelManager();
    @Unique
    private int syncedLevelExperience = -99999999;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public void addLevelExperience(int experience) {
        if (!levelManager.isMaxLevel()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
            levelManager.setLevelProgress(levelManager.getLevelProgress() + Math.max((float) experience / levelManager.getNextLevelExperience(), 0));
            levelManager.setTotalLevelExperience(MathHelper.clamp(levelManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));

            while (levelManager.getLevelProgress() >= 1.0F && !levelManager.isMaxLevel()) {
                levelManager.setLevelProgress((levelManager.getLevelProgress() - 1.0F) * (float) levelManager.getNextLevelExperience());
                levelManager.addExperienceLevels(1);
                levelManager.setLevelProgress(levelManager.getLevelProgress() / levelManager.getNextLevelExperience());

                PacketHelper.updateLevels(serverPlayerEntity);
                CriteriaInit.LEVEL_UP.trigger(serverPlayerEntity);
                serverPlayerEntity.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                serverPlayerEntity.getEntityWorld().getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity, ScoreAccess::incrementScore);
                if (levelManager.getOverallLevel() > 0) {
                    serverPlayerEntity.getEntityWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, serverPlayerEntity.getSoundCategory(), 1.0F, 1.0F);
                }
            }
        }
        this.syncedLevelExperience = -1;
    }

    @Inject(method = "playerTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;totalExperience:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void playerTickMixin(CallbackInfo info) {
        if (levelManager.getTotalLevelExperience() != this.syncedLevelExperience) {
            this.syncedLevelExperience = levelManager.getTotalLevelExperience();
            PacketHelper.updateLevels((ServerPlayerEntity) (Object) this);
        }

    }


    @Inject(method = "playerTick", at = @At("HEAD"))
    private void setContext(CallbackInfo ci) {
        // Seta o player exclusivo para esta thread de processamento
        LevelManager.CURRENT_MINER.set((ServerPlayerEntity)(Object)this);
    }

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void clearContext(CallbackInfo ci) {
        // LIMPA TUDO. Isso impede que o player permissivo "vaze" para o próximo cálculo.
        LevelManager.CURRENT_MINER.remove();
    }



}

