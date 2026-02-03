package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.GuiBannerResultRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;

public class GuiBannerResultRenderer extends PictureInPictureRenderer<GuiBannerResultRenderState> {
   private final MaterialSet materials;

   public GuiBannerResultRenderer(final MultiBufferSource.BufferSource bufferSource, final MaterialSet materials) {
      super(bufferSource);
      this.materials = materials;
   }

   public Class<GuiBannerResultRenderState> getRenderStateClass() {
      return GuiBannerResultRenderState.class;
   }

   protected void renderToTexture(final GuiBannerResultRenderState renderState, final PoseStack poseStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      poseStack.translate(0.0F, 0.25F, 0.0F);
      FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
      BannerRenderer.submitPatterns(this.materials, poseStack, submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, renderState.flag(), 0.0F, ModelBakery.BANNER_BASE, true, renderState.baseColor(), renderState.resultBannerPatterns(), false, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
      featureRenderDispatcher.renderAllFeatures();
   }

   protected String getTextureLabel() {
      return "banner result";
   }
}
