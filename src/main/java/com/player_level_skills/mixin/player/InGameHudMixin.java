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

        boolean hasAvailableLevel = ((LevelManagerAccess) client.player).getLevelManager().hasAvailableLevel();
        if (!hasAvailableLevel) {
            return;
        }

        Text text = Text.literal(Integer.toString(level));
        int x = context.getScaledWindowWidth() / 2 - textRenderer.getWidth(text) / 2;
        int y = context.getScaledWindowHeight() - 35;

        // sombra preta manual
        context.drawText(textRenderer, text, x + 1, y, 0xFF000000, false);
        context.drawText(textRenderer, text, x - 1, y, 0xFF000000, false);
        context.drawText(textRenderer, text, x, y + 1, 0xFF000000, false);
        context.drawText(textRenderer, text, x, y - 1, 0xFF000000, false);

        // texto principal em ciano
        context.drawText(textRenderer, text, x, y, 0xFF00E5FF, false);

        ci.cancel();
    }
}