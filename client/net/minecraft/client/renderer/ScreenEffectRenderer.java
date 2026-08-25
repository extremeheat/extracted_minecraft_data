package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ScreenEffectRenderer {
   private static final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");
   private final GameRenderer gameRenderer;
   private final SpriteGetter sprites;

   public ScreenEffectRenderer(final GameRenderer gameRenderer, final SpriteGetter sprites) {
      super();
      this.gameRenderer = gameRenderer;
      this.sprites = sprites;
   }

   public void submit(final float partialTicks, final SubmitNodeCollector submitNodeCollector, final PlayerRenderState playerRenderState, final CameraRenderState cameraRenderState, final boolean hideGui) {
      PoseStack poseStack = new PoseStack();
      AvatarRenderState avatarRenderState = playerRenderState.avatarRenderState;
      if (!cameraRenderState.entityRenderState.isSleeping && playerRenderState.hasPlayer && avatarRenderState != null) {
         if (playerRenderState.blockOverlay != null) {
            submitBlockSprite(playerRenderState.blockOverlay.atlasLocation(), playerRenderState.blockOverlay.u0(), playerRenderState.blockOverlay.v0(), playerRenderState.blockOverlay.u1(), playerRenderState.blockOverlay.v1(), poseStack, submitNodeCollector, -15132391);
         }

         if (cameraRenderState.isFirstPerson && !avatarRenderState.isSpectator) {
            if (playerRenderState.waterOverlay != null) {
               submitWater(playerRenderState.waterOverlay, poseStack, submitNodeCollector);
            }

            if (playerRenderState.isOnFire) {
               TextureAtlasSprite fireSprite = this.sprites.get(ModelBakery.FIRE_1);
               submitFire(poseStack, submitNodeCollector, fireSprite);
            }
         }
      }

      if (!hideGui) {
         this.renderItemActivationAnimation(playerRenderState, poseStack, partialTicks, submitNodeCollector);
      }

   }

   private void renderItemActivationAnimation(final PlayerRenderState playerRenderState, final PoseStack poseStack, final float partialTicks, final SubmitNodeCollector submitNodeCollector) {
      PlayerRenderState.ItemActivationRenderState itemActivation = playerRenderState.itemActivation;
      if (itemActivation != null) {
         int tick = 40 - itemActivation.ticks;
         float scale = ((float)tick + partialTicks) / 40.0F;
         float ts = scale * scale;
         float tc = scale * ts;
         float smoothScale = 10.25F * tc * ts - 24.95F * ts * ts + 25.5F * tc - 13.8F * ts + 4.0F * scale;
         float piScale = smoothScale * 3.1415927F;
         WindowRenderState windowState = this.gameRenderer.gameRenderState().windowRenderState;
         float aspectRatio = (float)windowState.width / (float)windowState.height;
         float offX = itemActivation.offX * 0.3F * aspectRatio;
         float offY = itemActivation.offY * 0.3F;
         poseStack.pushPose();
         poseStack.translate(offX * Mth.abs(Mth.sin((double)(piScale * 2.0F))), offY * Mth.abs(Mth.sin((double)(piScale * 2.0F))), -10.0F + 9.0F * Mth.sin((double)piScale));
         float size = 0.8F;
         poseStack.scale(0.8F, 0.8F, 0.8F);
         poseStack.rotateDegrees(Axis.YP, 900.0F * Mth.abs(Mth.sin((double)piScale)));
         poseStack.rotateDegrees(Axis.XP, 6.0F * Mth.cos((double)(scale * 8.0F)));
         poseStack.rotateDegrees(Axis.ZP, 6.0F * Mth.cos((double)(scale * 8.0F)));
         this.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
         itemActivation.itemState.submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
   }

   private static void submitBlockSprite(final Identifier atlasLocation, final float u0, final float v0, final float u1, final float v1, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int color) {
      submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.blockScreenEffect(atlasLocation), (pose, builder) -> buildQuad(builder, pose.pose(), -1.0F, -1.0F, 1.0F, 1.0F, -0.5F, u1, v1, u0, v0, color));
   }

   private static void submitWater(final PlayerRenderState.WaterOverlay waterOverlay, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector) {
      submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.blockScreenEffect(UNDERWATER_LOCATION), (pose, builder) -> {
         float uvSize = 4.0F;
         buildQuad(builder, pose.pose(), -1.0F, -1.0F, 1.0F, 1.0F, -0.5F, waterOverlay.uOffset() + 4.0F, waterOverlay.vOffset() + 4.0F, waterOverlay.uOffset(), waterOverlay.vOffset(), waterOverlay.color());
      });
   }

   private static void submitFire(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final TextureAtlasSprite sprite) {
      submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.fireScreenEffect(sprite.atlasLocation()), (basePose, builder) -> {
         Matrix4f pose = new Matrix4f();
         pose.set(basePose.pose());
         pose.translate(0.24F, -0.3F, 0.0F);
         pose.rotateY(-0.17453292F);
         buildFireQuad(sprite, builder, pose);
         pose.set(basePose.pose());
         pose.translate(-0.24F, -0.3F, 0.0F);
         pose.rotateY(0.17453292F);
         buildFireQuad(sprite, builder, pose);
      });
   }

   private static void buildFireQuad(final TextureAtlasSprite sprite, final VertexConsumer builder, final Matrix4f pose) {
      float size = 1.0F;
      buildSpriteQuad(builder, pose, sprite, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, -436207617);
   }

   private static void buildSpriteQuad(final VertexConsumer builder, final Matrix4f pose, final TextureAtlasSprite sprite, final float x0, final float y0, final float x1, final float y1, final float z, final int color) {
      buildQuad(builder, pose, x0, y0, x1, y1, z, sprite.getU1(), sprite.getV1(), sprite.getU0(), sprite.getV0(), color);
   }

   private static void buildQuad(final VertexConsumer builder, final Matrix4f pose, final float x0, final float y0, final float x1, final float y1, final float z, final float u0, final float v0, final float u1, final float v1, final int color) {
      builder.addVertex((Matrix4fc)pose, x0, y0, z).setUv(u0, v0).setColor(color);
      builder.addVertex((Matrix4fc)pose, x1, y0, z).setUv(u1, v0).setColor(color);
      builder.addVertex((Matrix4fc)pose, x1, y1, z).setUv(u1, v1).setColor(color);
      builder.addVertex((Matrix4fc)pose, x0, y1, z).setUv(u0, v1).setColor(color);
   }
}
