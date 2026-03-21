package com.player_level_skills.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.player_level_skills.screen.PlayerLevelSkillsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    protected HandledScreenMixin(Text title) { super(title); }


    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow protected T handler;

    @Unique private static final Identifier ATTRIBUTE_BOOK =
            Identifier.of("attributescreen", "textures/gui/attribute_book.png");


    @Unique private int attributespanel$iconTick = 0;
    @Unique private PlayerLevelSkillsScreen player_level_skill$playerlevelSkillsScreen;

    @Unique
    private boolean attributespanel$shouldAttach() {
        return this.handler instanceof PlayerScreenHandler;
    }


    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void attributespanel$onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!attributespanel$shouldAttach()) return;

        attributespanel$iconTick++;
        if (player_level_skill$playerlevelSkillsScreen != null) {
            player_level_skill$playerlevelSkillsScreen.tick();

            int iconSize = 9;
            int buttonX = this.x + this.backgroundWidth / 2 + (-61);
            int buttonY = this.y + 10;

            boolean hovered = mouseX >= buttonX && mouseX <= buttonX + iconSize &&
                    mouseY >= buttonY && mouseY <= buttonY + iconSize;

            int color = hovered ? 0xFFFFFFFF : 0xA9A9A9A9;


            boolean animate = true;
            if (hovered && animate) {
                float time = attributespanel$iconTick / 16f;
                float pulse = (float) Math.sin(time);
                float scale = 1.0f + 0.1f * pulse;



                context.getMatrices().pushMatrix();
                context.getMatrices().translate(buttonX + iconSize / 2f, buttonY + iconSize / 2f);
                context.getMatrices().scale(scale, scale);
                context.getMatrices().translate(-iconSize / 2f, -iconSize / 2f);
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ATTRIBUTE_BOOK, 0, 0, 0, 0, iconSize, iconSize, 9, 9, color);
                context.getMatrices().popMatrix();
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ATTRIBUTE_BOOK, buttonX, buttonY, 0, 0,iconSize,iconSize,9,9, color);
            }

            if (hovered) {
                context.drawTooltip(textRenderer,Text.translatable("attributepanel.tooltip.button"), mouseX, mouseY);
            }
        }
    }


    @Inject(method = "renderMain", at = @At("RETURN"))
    private void attributespanel$renderTooltipAfterEverything(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!attributespanel$shouldAttach()) return;
        if (player_level_skill$playerlevelSkillsScreen != null && player_level_skill$playerlevelSkillsScreen.isExpanded()) {
        //    player_level_skill$playerlevelSkillsScreen.renderTooltip(context);
        }
    }


    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void attributespanel$onMouseClick(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!attributespanel$shouldAttach()) return;

        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (player_level_skill$playerlevelSkillsScreen != null) {
            int iconSize = 9;
            int buttonX = this.x + this.backgroundWidth / 2 + (-61);
            int buttonY = this.y + 10;

            // Verificação do clique no ícone do painel
            if (mouseX >= buttonX && mouseX <= buttonX + iconSize &&
                    mouseY >= buttonY && mouseY <= buttonY + iconSize) {
                player_level_skill$playerlevelSkillsScreen.toggle();
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }

            // Lógica de interação com o painel aberto
            if (player_level_skill$playerlevelSkillsScreen.mouseClicked(mouseX, mouseY, button)) {

                this.setFocused(player_level_skill$playerlevelSkillsScreen);
                if (button == 0) {
                    this.setDragging(true);

                }
                cir.setReturnValue(true);
                cir.cancel();

            }
        }
    }
    // ===== INJECT: mouseDragged ✅ NOVO =====
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void attributespanel$onMouseDragged(Click click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        double deltaX = x;
        double deltaY = y;

        if (!attributespanel$shouldAttach()) return;

        if (player_level_skill$playerlevelSkillsScreen != null) {

            boolean handled = player_level_skill$playerlevelSkillsScreen.mouseDragged(mouseX, mouseY, button,
                    deltaX, deltaY);

            if (handled) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    // ===== INJECT: mouseReleased ✅ NOVO =====
    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void player_level_skill$onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (!attributespanel$shouldAttach()) return;

        if (player_level_skill$playerlevelSkillsScreen != null) {
            boolean handled = player_level_skill$playerlevelSkillsScreen.mouseReleased(mouseX, mouseY, button);

            if (handled) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}


