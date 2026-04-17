package com.player_level_skills.mixin.network;

import com.player_level_skills.level.LevelManager;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
//    @Shadow
//    @Final
//    protected ServerPlayerEntity player;
//
//    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
//    private void player_level_skills$markMiner(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
//        // 'player' é um campo desta classe no Yarn
//        LevelManager.CURRENT_MINER.set(this.player);
//        System.out.println("[DEBUG] processBlockBreakingAction: Cap player "+ player.getName().toString());
//    }
//
//    @Inject(method = "processBlockBreakingAction", at = @At("TAIL"))
//    private void player_level_skills$unmarkMiner(CallbackInfo ci) {
//        LevelManager.CURRENT_MINER.remove();
//        System.out.println("[DEBUG] processBlockBreakingAction: Remove player "+ player.getName().toString());
//    }

//    @Inject(method = "onBlockBreakingAction", at = @At("HEAD"))
//    private void player_level_skills$markBreak(BlockPos pos, boolean success, int sequence, String reason, CallbackInfo ci) {
//        // 'player' é um campo desta classe no Yarn
//        LevelManager.CURRENT_ATTACKER.set(this.player);
//        System.out.println("[DEBUG] processBlockBreakingAction: Cap player "+ player.getName().toString());
//    }
//
//    @Inject(method = "onBlockBreakingAction", at = @At("TAIL"))
//    private void player_level_skills$unmarkBreak(CallbackInfo ci) {
//        LevelManager.CURRENT_ATTACKER.remove();
//        System.out.println("[DEBUG] processBlockBreakingAction: Remove player "+ player.getName().toString());
//    }
//
//    @Inject(method = "continueMining", at = @At("HEAD"))
//    private void player_level_skills$setMinerContext(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
//        // Define o minerador apenas durante a execução da quebra do bloco
//        LevelManager.CURRENT_MINER.set(this.player);
//    }
//
//    @Inject(method = "continueMining", at = @At("TAIL"))
//    private void player_level_skills$clearMinerContext(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
//        // Limpeza garantida após o cálculo dos drops
//        LevelManager.CURRENT_MINER.remove();
//    }

}

