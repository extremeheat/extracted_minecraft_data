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

   public GuiBannerResultRenderer(MultiBufferSource.BufferSource var1, MaterialSet var2) {
      super(var1);
      this.materials = var2;
   }

   public Class<GuiBannerResultRenderState> getRenderStateClass() {
      return GuiBannerResultRenderState.class;
   }

   protected void renderToTexture(GuiBannerResultRenderState var1, PoseStack var2) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      var2.translate(0.0F, 0.25F, 0.0F);
      FeatureRenderDispatcher var3 = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      SubmitNodeStorage var4 = var3.getSubmitNodeStorage();
      BannerRenderer.submitPatterns(this.materials, var2, var4, 15728880, OverlayTexture.NO_OVERLAY, var1.flag(), 0.0F, ModelBakery.BANNER_BASE, true, var1.baseColor(), var1.resultBannerPatterns(), false, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
      var3.renderAllFeatures();
   }

   protected String getTextureLabel() {
      return "banner result";
   }
}
