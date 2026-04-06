package com.player_level_skills.mixin.block;

import com.player_level_skills.access.LevelManagerAccess;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    private void onExplodedMixin(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci) {
        if (explosion.getCausingEntity() instanceof PlayerEntity playerEntity
                && !playerEntity.isCreative()
                && !((LevelManagerAccess) playerEntity).getLevelManager().hasRequiredMiningLevel(state.getBlock())) {
            ci.cancel();
        }
    }
}