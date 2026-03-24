package com.player_level_skills.entity.render;

import com.player_level_skills.entity.LevelExperienceOrbEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity, LevelExperienceOrbEntityRenderer.OrbRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");

    public LevelExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.shadowOpacity = 0.75F;
    }

    @Override
    public OrbRenderState createRenderState() {
        return new OrbRenderState();
    }

    @Override
    public void updateRenderState(LevelExperienceOrbEntity orb, OrbRenderState state, float tickDelta) {
        super.updateRenderState(orb, state, tickDelta);
        state.age = orb.age;
        state.orbSize = orb.getOrbSize();
    }

    @Override
    protected int getBlockLight(LevelExperienceOrbEntity orb, BlockPos pos) {
        return MathHelper.clamp(super.getBlockLight(orb, pos) + 7, 0, 15);
    }

    @Override
    public void render(OrbRenderState state, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrixStack.push();

        int j = state.orbSize * 100;
        float h = (float)(j % 4 * 16) / 64.0F;
        float k = (float)(j % 4 * 16 + 16) / 64.0F;
        float l = (float)(j / 4 * 16) / 64.0F;
        float m = (float)(j / 4 * 16 + 16) / 64.0F;

        float r = (float)state.age / 2.0F;
        int red = 30;
        int green = Math.max(190, (int)((MathHelper.sin(r + (float)(Math.PI * 2.0 / 3.0)) + 1.0F) * 0.7F * 255.0F));
        int blue = Math.max(240, (int)((MathHelper.sin(r + 0.0F) + 1.0F) * 0.85F * 255.0F));
        int alpha = 255;

        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.multiply(this.dispatcher.camera.getRotation());
        matrixStack.scale(0.3F, 0.3F, 0.3F);

        MatrixStack.Entry entry = matrixStack.peek();

        queue.submitCustom(matrixStack, RenderLayers.itemEntityTranslucentCull(TEXTURE), new OrderedRenderCommandQueue.Custom() {
            @Override
            public void render(MatrixStack.Entry matricesEntry, VertexConsumer vertexConsumer) {
                vertex(vertexConsumer, matricesEntry, -0.5F, -0.25F, red, green, blue, alpha, h, m);
                vertex(vertexConsumer, matricesEntry, 0.5F, -0.25F, red, green, blue, alpha, k, m);
                vertex(vertexConsumer, matricesEntry, 0.5F, 0.75F, red, green, blue, alpha, k, l);
                vertex(vertexConsumer, matricesEntry, -0.5F, 0.75F, red, green, blue, alpha, h, l);
            }
        });

        matrixStack.pop();
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry,
                               float x, float y, int red, int green, int blue, int alpha,
                               float u, float v) {
        vertexConsumer.vertex(entry, x, y, 0.0F)
                .color(red, green, blue, alpha)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(15728880)
                .normal(entry, 0.0F, 1.0F, 0.0F);
    }

    public static class OrbRenderState extends EntityRenderState {
        public int orbSize;
        public int age;
    }
}


//@Environment(EnvType.CLIENT)
//public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity, EntityRenderState> {
//    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");
//    private static final RenderLayer LAYER = RenderLayers.entityTranslucentEmissive(TEXTURE);
//
//    public LevelExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
//        super(context);
//        this.shadowRadius = 0.15f;
//        this.shadowOpacity = 0.75f;
//    }
//
//    @Override
//    protected int getBlockLight(LevelExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
//        return MathHelper.clamp(super.getBlockLight(experienceOrbEntity, blockPos) + 7, 0, 15);
//    }
//
//    @Override
//    public EntityRenderState createRenderState() {
//        return null;
//    }
//
//    //@Override
//    public void render(LevelExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
//        matrixStack.push();
//        int j = experienceOrbEntity.getOrbSize()*100;
//        float h = (float)(j % 4 * 16) / 64.0F;
//        float k = (float)(j % 4 * 16 + 16) / 64.0F;
//        float l = (float)(j / 4 * 16) / 64.0F;
//        float m = (float)(j / 4 * 16 + 16) / 64.0F;
//        float r = ((float)experienceOrbEntity.age + g) / 2.0F;
//        int s = Math.max(80,(int)((MathHelper.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F));
//        int u = Math.max(100,(int)((MathHelper.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F));
//        matrixStack.translate(0.0F, 0.1F, 0.0F);
//        matrixStack.multiply(this.dispatcher.camera.getRotation());
//        matrixStack.scale(0.3F, 0.3F, 0.3F);
//        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
//        MatrixStack.Entry entry = matrixStack.peek();
//        vertex(vertexConsumer, entry, -0.5F, -0.25F, u,s, 255, h, m, i);
//        vertex(vertexConsumer, entry, 0.5F, -0.25F, u,s, 255, k, m, i);
//        vertex(vertexConsumer, entry, 0.5F, 0.75F, u,s, 255, k, l, i);
//        vertex(vertexConsumer, entry, -0.5F, 0.75F, u,s, 255, h, l, i);
//        matrixStack.pop();
//        render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
//    }
//
//    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
//        vertexConsumer.vertex(matrix, x, y, 0.0F).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0F, 1.0F, 0.0F);
//    }
//
//    //@Override
//    public Identifier getTexture(LevelExperienceOrbEntity experienceOrbEntity) {
//        return TEXTURE;
//    }
//}
