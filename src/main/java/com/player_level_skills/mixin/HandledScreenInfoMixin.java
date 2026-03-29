package com.player_level_skills.mixin;

import com.player_level_skills.Player_level_skills;
import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenInfoMixin<T extends ScreenHandler> extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow protected T handler;

    @Unique
    private static final Identifier SKILL_ICON = Identifier.of(Player_level_skills.MOD_ID, "textures/gui/skill_book.png");

    protected HandledScreenInfoMixin(Text title) {
        super(title);
    }

    @Unique
    private boolean player_level_skills$shouldAttach() {
        return this.handler instanceof net.minecraft.screen.PlayerScreenHandler;
    }

    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void player_level_skills$drawBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!player_level_skills$shouldAttach()) {
            return;
        }

        assert this.client != null;
        assert this.client.player != null;

        LevelManager levelManager = ((LevelManagerAccess) this.client.player).getLevelManager();

        int iconSize = 9;
        int buttonX = this.x + this.backgroundWidth / 2 + ConfigInit.CONFIG.inventorySkillLevelPosX;
        int buttonY = this.y + ConfigInit.CONFIG.inventorySkillLevelPosY;
        int textX = (56 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.x);
        int textY = (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.y + textRenderer.fontHeight / 2);

        boolean hovered = mouseX >= buttonX && mouseX <= buttonX + iconSize
                && mouseY >= buttonY && mouseY <= buttonY + iconSize;

        boolean hovereded = mouseX >= textX && mouseX <= textX + 14
                && mouseY >= (textY-3) && mouseY <= textY + 2;

        if (ConfigInit.CONFIG.inventorySkillLevel) {




            int color = 0xFFFFFFFF;
            if (levelManager.getSkillPoints() > 0) {
                color = 0xFF00E5FF;
            }

            context.getMatrices().pushMatrix();
            context.getMatrices().scale(0.6F, 0.6F);
            context.getMatrices().translate(
                    (56 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.x) / 0.6F,
                    (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.y + textRenderer.fontHeight / 2F) / 0.6F);
            context.drawText(this.textRenderer, Text.translatable("text.levelz.gui.short_level", levelManager.getOverallLevel()), 0, -textRenderer.fontHeight / 2, color, false);
            context.getMatrices().popMatrix();
            if (hovereded) {
                context.drawTooltip(this.textRenderer, Text.translatable("key.player_level_skills.open_screen", levelManager.getOverallLevel()).formatted(Formatting.AQUA), mouseX, mouseY);
            }
        }
// Icone pré preparado
//        int color = hovered ? 0xFFFFFFFF : 0xA9A9A9A9;
//
//        context.drawTexture(RenderPipelines.GUI_TEXTURED, SKILL_ICON, buttonX, buttonY, 0, 0, iconSize, iconSize, 9, 9, color);
//
//        if (hovered) {
//            int color2 = 0xFFFFFFFF;
//            if (levelManager.getSkillPoints() > 0) {
//                color2 = 0xFF55FF55;
//            }
//            context.drawTooltip(this.textRenderer, Text.translatable("key.player_level_skills.open_screen", levelManager.getOverallLevel()).formatted(Formatting.RED), mouseX, mouseY);
//        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void player_level_skills$onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!player_level_skills$shouldAttach()) {
            return;
        }

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        int iconSize = 9;
        int buttonX = this.x + this.backgroundWidth / 2 + ConfigInit.CONFIG.inventorySkillLevelPosX;
        int buttonY = this.y + ConfigInit.CONFIG.inventorySkillLevelPosY;
        int textX = (56 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.x);
        int textY = (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.y + textRenderer.fontHeight / 2);

        if (mouseX >= textX && mouseX <= textX + 14
                && mouseY >= (textY-3) && mouseY <= textY + 2) {

            assert this.client != null;

            this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.client.setScreen(null);

            this.client.execute(() -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.setScreen(new PlayerLevelSkillsScreen());
                }
            });

            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void player_level_skills$onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (!player_level_skills$shouldAttach()) {
            return;
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void player_level_skills$onMouseDragged(Click click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
        if (!player_level_skills$shouldAttach()) {
            return;
        }
    }
}