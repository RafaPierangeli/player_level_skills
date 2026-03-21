package com.player_level_skills.screen;

import com.player_level_skills.Player_level_skills;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.level.SkillAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import java.util.*;

@Environment(EnvType.CLIENT)

public class PlayerLevelSkillsScreen extends Screen {

    public static final Identifier BACKGROUND_TEXTURE = Identifier.of(Player_level_skills.MOD_ID, "textures/gui/skill_background.png");

    public static final Identifier BACKGROUND_TEXTURE2 = Identifier.of(Player_level_skills.MOD_ID, "textures/gui/skill_background2.png");

    public static final Identifier ATTRIBUTE_BACKGROUND_TEXTURE = Identifier.of(Player_level_skills.MOD_ID, "textures/gui/attribute_background.png");

    public static final Identifier ICON_TEXTURE = Identifier.of(Player_level_skills.MOD_ID, "textures/gui/icons.png");

    private final int backgroundWidth = 200;
    private final int backgroundHeight = 215;

    private int x;
    private int y;

    private LevelManager levelManager;
    private ClientPlayerEntity clientPlayerEntity;
    private final Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI).rotateLocalY(2.7f);
    private boolean turnClientPlayer = false;

    protected int currentPage = 0;

    private List<SkillAttribute> attributes = new ArrayList<>();

    private boolean showAttributes = false;
    private int attributeRow = 0;

    private final WidgetButtonPage[] levelButtons = new WidgetButtonPage[12];
    private int skillRow = 0;

    private final List<Integer> playerSkills = new ArrayList<>();

    public PlayerLevelSkillsScreen() {
        super(Text.translatable("screen.player_level_skills.title"));
    }

    //ADD Rafa
    public boolean isExpanded() {
        return turnClientPlayer;
    }

    public void toggle() {
        turnClientPlayer = !turnClientPlayer;
        currentPage = 0;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        if (this.client != null && this.client.interactionManager != null && this.client.player != null && this.client.world != null) {
            PlayerInput playerInput = new PlayerInput(
                    false, // forward
                    false, // backward
                    false, // left
                    false, // right
                    false, // jumping
                    false, // sneaking
                    false
            );

            this.clientPlayerEntity = this.client.interactionManager.createPlayer(
                    this.client.world,
                    this.client.player.getStatHandler(),
                    this.client.player.getRecipeBook(),
                    playerInput,
                    false
            );
            //((ClientPlayerAccess) this.clientPlayerEntity).setShouldRenderClientName(false);
            //byte playerModelParts = this.client.player.getDataTracker().get(PlayerEntityAccessor.getPLAYER_MODEL_PARTS());
            //this.clientPlayerEntity.getDataTracker().set(PlayerEntityAccessor.getPLAYER_MODEL_PARTS(), playerModelParts);

            if (this.clientPlayerEntity != null) {
                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                    if (!this.client.player.getEquippedStack(equipmentSlot).isEmpty()) {
                        this.clientPlayerEntity.equipStack(equipmentSlot, this.client.player.getEquippedStack(equipmentSlot));
                    }
                }
            }
        }

        if (this.playerSkills.isEmpty()) {
            for (int i = 0; i < 18; i++) {
                this.playerSkills.add(i);
            }
        }

        for (int i = 0; i < 12; i++) {
            if (this.playerSkills.size() <= i) {
                break;
            }

            final int skillId = i;
            this.levelButtons[i] = this.addDrawableChild(
                    new WidgetButtonPage(
                            this.x + (i % 2 == 0 ? 80 : 169),
                            this.y + 91 + i / 2 * 20,
                            13,
                            13,
                            33,
                            42,
                            true,
                            true,
                            null,
                            button -> {
                                assert this.client != null;
                                this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            }
                    )
            );
        }

        updateLevelButtons();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        if (this.client != null && this.client.player != null) {
            Text title = Text.translatable("screen.player_level_skills.player_title", this.client.player.getName().getString());
            context.drawText(this.textRenderer, title, this.x + 118 - this.textRenderer.getWidth(title) / 2, this.y + 7, 0xFF3F3F3F, false);

            if (!this.attributes.isEmpty()) {
                if (this.showAttributes) {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 30, 114, 15, 13, 256, 256);
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 202, this.y, 0, 0, 82, 215, 200, 215);
                    int maxAttributes = Math.min(this.attributes.size(), 15);
                    if (this.attributes.size() > 15) {
                        int scrollLevels = this.attributes.size() - 15;
                        int sliderY = this.attributeRow * 158 / scrollLevels;
                        context.drawTexture(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8 + sliderY, 82, 0, 6, 41, 256, 256);
                    } else {
                        context.drawTexture(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8, 88, 0, 6, 41, 256, 256);
                    }
                    context.drawText(this.textRenderer, Text.translatable("text.levelz.gui.attributes"), this.x + 214, this.y + 12, 0xFFE0E0E0, false);

                    int k = 27;
                    for (int i = this.attributeRow; i < this.attributeRow + maxAttributes; i++) {
                        String attributeKey = "teste";
                         if (attributeKey.contains(":")) {
                            attributeKey = attributeKey.split(":")[1];
                        }
                        context.drawTexture(RenderPipelines.GUI_TEXTURED,Player_level_skills.identifierOf("textures/gui/sprites/" + attributeKey + ".png"), this.x + 214, this.y + k, 0, 0, 9, 9, 9, 9);
                        float attributeValue = 10;
                                //(float) Math.round(this.client.player.getAttributeInstance(this.attributes.get(i).getAttibute()).getValue() * 100.0D) / 100.0F;
                        context.drawText(this.textRenderer, Text.of(String.valueOf(attributeValue)), this.x + 214 + 15, this.y + k, 0xFFE0E0E0, false);

                        k += 12;
                    }
                } else {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 15, 114, 15, 13, 256, 256);
                }
                if (isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
                    context.drawTooltip(this.textRenderer, Text.translatable("text.levelz.gui.attributes"), mouseX, mouseY);
                }
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 0, 114, 15, 13, 256, 256);
            }

            // Level label
            Text skillLevelText = Text.translatable("text.levelz.gui.level"); // this.levelManager.getOverallLevel()
            context.drawText(this.textRenderer, skillLevelText, this.x + 62, this.y + 42, 0xFF3F3F3F, false);
            // Point label
            Text skillPointText = Text.translatable("text.levelz.gui.points"); //this.levelManager.getSkillPoints()
            context.drawText(this.textRenderer, skillPointText, this.x + 62, this.y + 54, 0xFF3F3F3F, false);

            // Experience bar
            context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 62, this.y + 21, 0, 100, 131, 5, 256, 256);

           // int nextLevelExperience = this.levelManager.getNextLevelExperience();
            //float levelProgress = this.levelManager.getLevelProgress();
            //long experience = (int) (nextLevelExperience * levelProgress);

            context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 62, this.y + 21, 0, 105, (int) (130.0f * 5), 5, 256, 256); // chance 5 to levelProgress
            // current xp label
            Text currentXpText = Text.translatable("text.levelz.gui.current_xp"); //, experience, nextLevelExperience
            context.drawText(this.textRenderer, currentXpText, this.x - this.textRenderer.getWidth(currentXpText) / 2 + 127, this.y + 30, 0xFF3F3F3F, false);

            if (!LevelManager.CRAFTING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 29, 14, 13, mouseX, mouseY)) {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 30, 80, 15, 13, 256, 256);
                    context.drawTooltip(this.textRenderer, Text.translatable("restriction.levelz.crafting"), mouseX, mouseY);
                } else {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 15, 80, 15, 13, 256, 256);
                }
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 0, 80, 15, 13, 256, 256);
            }

            if (!LevelManager.MINING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 45, 14, 13, mouseX, mouseY)) {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 75, 80, 15, 13, 256, 256);
                    context.drawTooltip(this.textRenderer, Text.translatable("restriction.levelz.mining"), mouseX, mouseY);
                } else {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 60, 80, 15, 13, 256, 256);
                }
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 45, 80, 15, 13, 256, 256);
            }
        }


        if (this.clientPlayerEntity != null) {

            //InventoryScreen.drawEntity( context, this.x + 33, this.y + 43, 30,30, 30, 30.0f, mouseX, mouseY, this.clientPlayerEntity);

            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ICON_TEXTURE, this.x + 9, this.y + 67, 0, 138, 15, 10,256,256);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ICON_TEXTURE, this.x + 9, this.y + 67, 0, 128, 15, 10,256,256);
            }
            if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ICON_TEXTURE, this.x + 41, this.y + 67, 15, 138, 15, 10,256,256);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,ICON_TEXTURE, this.x + 41, this.y + 67, 15, 128, 15, 10,256,256);
            }

        }


    }



    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE2, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight,this.backgroundWidth,this.backgroundHeight);

        for (int i = 0; i < 12; i++) {
            int skillId = i + this.skillRow * 2;
            if (LevelManager.SKILLS.size() <= skillId) {
                break;
            }
            //if (this.levelManager.getPlayerSkills().size() <= skillId) {
            //    break;
            //}
            context.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE, this.x + (i % 2 == 0 ? 8 : 96), this.y + 87 + i / 2 * 20, 0, 215, 88, 20,88,20);
            context.drawTexture(RenderPipelines.GUI_TEXTURED,Player_level_skills.identifierOf("textures/gui/sprites/" + LevelManager.SKILLS.get(skillId).getKey() + ".png"), this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 0, 0, 16, 16, 16, 16);

            Text skillLevel = Text.translatable("text.levelz.gui.current_level", this.levelManager.getSkillLevel(skillId), LevelManager.SKILLS.get(skillId).getMaxLevel());
            context.drawText(this.textRenderer, skillLevel, this.x + (i % 2 == 0 ? 53 : 141) - this.textRenderer.getWidth(skillLevel) / 2, this.y + 94 + i / 2 * 20, 0x3F3F3F, false);

            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, LevelManager.SKILLS.get(skillId).getText(), mouseX, mouseY);
            }
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE, this.x + 186, this.y + 87, 206, 0, 6, 34,6,34);
        //if (this.levelManager.getPlayerSkills().size() > 12) {
        //    int scrollLevels = (this.levelManager.getPlayerSkills().size() - 12) / 2;
        //    if (this.levelManager.getPlayerSkills().size() % 2 != 0) {
        //        scrollLevels += 1;
        //    }
//
         //    int sliderY = this.skillRow * 86 / scrollLevels;
        //    context.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE, this.x + 186, this.y + 87 + sliderY, 200, 0, 6, 34, 6, 34);
        //} else {
        //    context.drawTexture(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE, this.x + 186, this.y + 87, 206, 0, 6, 34,6,34);
        //}
        //DrawTabHelper.drawTab(client, context, this, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.clientPlayerEntity != null && this.turnClientPlayer) {
            double mouseX = this.client.mouse.getX() * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth();
            double mouseY = this.client.mouse.getY() * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight();

            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.quaternionf.rotateLocalY(0.087f);
            } else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.quaternionf.rotateLocalY(-0.087f);
            } else {
                this.turnClientPlayer = false;
            }
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(input)) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    //@Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.turnClientPlayer) {
            this.turnClientPlayer = false;
        }
        return false;
    }

    //@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.attributes.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
            this.showAttributes = !this.showAttributes;
            this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (this.clientPlayerEntity != null) {
            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        for (int i = 0; i < 12; i++) {
            int skillId = i + this.skillRow * 2;
            if (skillId >= this.playerSkills.size()) {
                break;
            }

            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.showAttributes && this.attributes.size() > 15 && isPointWithinBounds(this.x + 209, this.y + 7, 68, 201, mouseX, mouseY)) {
            int maxAttributeRow = this.attributes.size() - 15;
            int newAttributeRow = this.attributeRow - (int) verticalAmount;
            if (newAttributeRow < 0) {
                this.attributeRow = 0;
            } else {
                this.attributeRow = Math.min(newAttributeRow, maxAttributeRow);
            }
            return true;
        }

        if (this.playerSkills.size() > 12 && isPointWithinBounds(this.x + 7, this.y + 86, 186, 122, mouseX, mouseY)) {
            int maxSkillRow = (this.playerSkills.size() - 12) / 2;
            if (this.playerSkills.size() % 2 != 0) {
                maxSkillRow += 1;
            }

            int newSkillRow = this.skillRow - (int) verticalAmount;
            if (newSkillRow < 0) {
                this.skillRow = 0;
            } else {
                this.skillRow = Math.min(newSkillRow, maxSkillRow);
            }

            updateLevelButtons();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!turnClientPlayer) return false;
        return mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }


    @Override
    public boolean shouldPause() {
        return false;
    }

    public void updateLevelButtons() {
        for (int i = 0; i < this.levelButtons.length; i++) {
            if (this.playerSkills.size() <= i) {
                break;
            }

            int skillId = i + this.skillRow * 2;
            if (skillId >= this.playerSkills.size()) {
                this.levelButtons[i].visible = false;
                return;
            } else {
                this.levelButtons[i].visible = true;
            }

            this.levelButtons[i].active = true;
        }
    }

    public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    private static class WidgetButtonPage extends ButtonWidget {

        private final boolean hoverOutline;
        private final boolean clickable;
        private final int textureX;
        private final int textureY;
        private final List<Text> tooltip = new ArrayList<>();
        private int clickedKey = -1;

        public WidgetButtonPage(int x, int y, int sizeX, int sizeY, int textureX, int textureY, boolean hoverOutline, boolean clickable, @Nullable Text tooltip, ButtonWidget.PressAction onPress) {
            super(x, y, sizeX, sizeY, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.hoverOutline = hoverOutline;
            this.clickable = clickable;
            this.textureX = textureX;
            this.textureY = textureY;
            this.width = sizeX;
            this.height = sizeY;

            if (tooltip != null) {
                this.tooltip.add(tooltip);
            }
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    ICON_TEXTURE,
                    this.getX(),
                    this.getY(),
                    this.textureX + this.getTextureY() * this.width,
                    this.textureY,
                    this.width,
                    this.height,
                    200,
                    215
            );

            if (this.isHovered()) {
                //context.drawTooltip(minecraftClient.textRenderer, this.tooltip, mouseX, mouseY);
            }
        }


        //@Override
       // public boolean mouseClicked(double mouseX, double mouseY, int button) {
       //     this.clickedKey = button;
        //    if (!this.clickable) {
       //         return false;
        //    }
         //   return super.mouseClicked(mouseX, mouseY, button);
       // }

        //@Override
        //protected boolean isValidClickButton(int button) {
        //    return super.isValidClickButton(button) || button == 1 || button == 2;
        //}

        //@Override
        //public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //    if (!this.clickable) {
        //        return false;
        //    }
        //    return super.keyPressed(keyCode, scanCode, modifiers);
        //}

        public boolean wasMiddleButtonClicked() {
            return clickedKey == 2;
        }

        public boolean wasRightButtonClicked() {
            return clickedKey == 1;
        }

        private int getTextureY() {
            int i = 1;
            if (!this.active) {
                i = 0;
            } else if (this.isHovered()) {
                i = 2;
            }
            return i;
        }
    }
}