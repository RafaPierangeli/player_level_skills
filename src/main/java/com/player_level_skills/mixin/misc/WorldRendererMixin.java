package com.player_level_skills.mixin.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.init.ConfigInit;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @Redirect(
            method = "renderTargetBlockOutline(Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/util/math/MatrixStack;ZLnet/minecraft/client/render/state/WorldRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDLnet/minecraft/client/render/state/OutlineRenderState;IF)V"
            )
    )
    private void redirectDrawBlockOutline(WorldRenderer instance,
                                          MatrixStack matrices,
                                          VertexConsumer vertexConsumer,
                                          double x,
                                          double y,
                                          double z,
                                          OutlineRenderState state,
                                          int color,
                                          float lineWidth) {

        if (this.client == null || this.client.player == null || this.client.world == null || !ConfigInit.CONFIG.highlightLocked) {
            ((WorldRendererInvoker) instance).invokeDrawBlockOutline(matrices, vertexConsumer, x, y, z, state, color, lineWidth);
            return;
        }

        BlockPos blockPos = ((OutlineRenderStateAccessor) (Object) state).getPos();
        BlockState blockState = this.client.world.getBlockState(blockPos);

        boolean locked = !((LevelManagerAccess) this.client.player)
                .getLevelManager()
                .hasRequiredMiningLevel(blockState.getBlock());

        int finalColor = locked ? 0x80FF0000 : color;

        ((WorldRendererInvoker) instance).invokeDrawBlockOutline(matrices, vertexConsumer, x, y, z, state, finalColor, lineWidth);
    }
}