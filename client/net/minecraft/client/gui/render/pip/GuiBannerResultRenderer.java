package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.gui.pip.GuiBannerResultRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;

public class GuiBannerResultRenderer extends PictureInPictureRenderer<GuiBannerResultRenderState> {
   private final SpriteGetter sprites;

   public GuiBannerResultRenderer(final MultiBufferSource.BufferSource bufferSource, final SpriteGetter sprites) {
      super(bufferSource);
      this.sprites = sprites;
   }

   public Class<GuiBannerResultRenderState> getRenderStateClass() {
      return GuiBannerResultRenderState.class;
   }

   protected void renderToTexture(final GuiBannerResultRenderState renderState, final PoseStack poseStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      poseStack.translate(0.0F, 0.25F, 0.0F);
      FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
      submitNodeStorage.submitModel(renderState.flag(), 0.0F, poseStack, 15728880, OverlayTexture.NO_OVERLAY, -1, Sheets.BANNER_BASE, this.sprites, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
      BannerRenderer.submitPatterns(this.sprites, poseStack, submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, renderState.flag(), 0.0F, true, renderState.baseColor(), renderState.resultBannerPatterns(), (ModelFeatureRenderer.CrumblingOverlay)null);
      featureRenderDispatcher.renderAllFeatures();
   }

   protected String getTextureLabel() {
      return "banner result";
   }
}
