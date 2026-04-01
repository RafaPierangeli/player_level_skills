package com.player_level_skills.mixin.misc;

import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OutlineRenderState.class)
public interface OutlineRenderStateAccessor {
    @Accessor("pos")
    BlockPos getPos();
}