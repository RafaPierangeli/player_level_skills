package com.player_level_skills.mixin.misc;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererInvoker {

    /**
     * Invoca o método private drawBlockOutline de forma segura.
     * Assinatura exata do Yarn 1.21.11+build.4.
     */
    @Invoker("drawBlockOutline")
    void invokeDrawBlockOutline(MatrixStack matrices,
                                VertexConsumer vertexConsumer,
                                double x,
                                double y,
                                double z,
                                OutlineRenderState state,
                                int color,
                                float lineWidth);
}