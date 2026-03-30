package com.player_level_skills.mixin.player;

import com.player_level_skills.access.LevelManagerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Bar.class)
public interface InGameHudMixin {

    @Inject(method = "drawExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void player_level_skills$drawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        int color = 0xFF55FF55;
        if (((LevelManagerAccess) client.player).getLevelManager().hasAvailableLevel()) {
            color = 0xFF00E5FF;
        }
        int shadow = 0XFF000000;

        Text text = Text.literal(Integer.toString(level));

        // posição vanilla aproximada: precisa ajustar se quiser pixel-perfect
        int x = context.getScaledWindowWidth() / 2 - textRenderer.getWidth(text) / 2;
        int y = context.getScaledWindowHeight() - 35;

        context.drawText(textRenderer, text, x, y, color, true);
        ci.cancel();
    }
}