package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class GuiEntityRenderer extends PictureInPictureRenderer<GuiEntityRenderState> {
   private final EntityRenderDispatcher entityRenderDispatcher;

   public GuiEntityRenderer(final MultiBufferSource.BufferSource bufferSource, final EntityRenderDispatcher entityRenderDispatcher) {
      super(bufferSource);
      this.entityRenderDispatcher = entityRenderDispatcher;
   }

   public Class<GuiEntityRenderState> getRenderStateClass() {
      return GuiEntityRenderState.class;
   }

   protected void renderToTexture(final GuiEntityRenderState entityState, final PoseStack poseStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      Vector3f translation = entityState.translation();
      poseStack.translate(translation.x, translation.y, translation.z);
      poseStack.mulPose((Quaternionfc)entityState.rotation());
      Quaternionf overriddenCameraAngle = entityState.overrideCameraAngle();
      FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      CameraRenderState cameraRenderState = new CameraRenderState();
      if (overriddenCameraAngle != null) {
         cameraRenderState.orientation = overriddenCameraAngle.conjugate(new Quaternionf()).rotateY(3.1415927F);
      }

      this.entityRenderDispatcher.submit(entityState.renderState(), cameraRenderState, 0.0, 0.0, 0.0, poseStack, featureRenderDispatcher.getSubmitNodeStorage());
      featureRenderDispatcher.renderAllFeatures();
   }

   protected float getTranslateY(final int height, final int guiScale) {
      return (float)height / 2.0F;
   }

   protected String getTextureLabel() {
      return "entity";
   }
}
