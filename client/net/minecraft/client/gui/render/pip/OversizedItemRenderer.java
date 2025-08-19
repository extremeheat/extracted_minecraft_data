package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.OversizedItemRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class OversizedItemRenderer extends PictureInPictureRenderer<OversizedItemRenderState> {
   private boolean usedOnThisFrame;
   @Nullable
   private Object modelOnTextureIdentity;

   public OversizedItemRenderer(MultiBufferSource.BufferSource var1) {
      super(var1);
   }

   public boolean usedOnThisFrame() {
      return this.usedOnThisFrame;
   }

   public void resetUsedOnThisFrame() {
      this.usedOnThisFrame = false;
   }

   public void invalidateTexture() {
      this.modelOnTextureIdentity = null;
   }

   public Class<OversizedItemRenderState> getRenderStateClass() {
      return OversizedItemRenderState.class;
   }

   protected void renderToTexture(OversizedItemRenderState var1, PoseStack var2) {
      var2.scale(1.0F, -1.0F, -1.0F);
      GuiItemRenderState var3 = var1.guiItemRenderState();
      ScreenRectangle var4 = var3.oversizedItemBounds();
      Objects.requireNonNull(var4);
      float var5 = (float)(var4.left() + var4.right()) / 2.0F;
      float var6 = (float)(var4.top() + var4.bottom()) / 2.0F;
      float var7 = (float)var3.x() + 8.0F;
      float var8 = (float)var3.y() + 8.0F;
      var2.translate((var7 - var5) / 16.0F, (var6 - var8) / 16.0F, 0.0F);
      TrackingItemStackRenderState var9 = var3.itemStackRenderState();
      boolean var10 = !var9.usesBlockLight();
      if (var10) {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      } else {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
      }

      FeatureRenderDispatcher var11 = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
      SubmitNodeStorage var12 = var11.getSubmitNodeStorage();
      var9.submit(var2, var12, 15728880, OverlayTexture.NO_OVERLAY, 0);
      var11.renderAllFeatures();
      this.modelOnTextureIdentity = var9.getModelIdentity();
   }

   public void blitTexture(OversizedItemRenderState var1, GuiRenderState var2) {
      super.blitTexture(var1, var2);
      this.usedOnThisFrame = true;
   }

   public boolean textureIsReadyToBlit(OversizedItemRenderState var1) {
      TrackingItemStackRenderState var2 = var1.guiItemRenderState().itemStackRenderState();
      return !var2.isAnimated() && var2.getModelIdentity().equals(this.modelOnTextureIdentity);
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1 / 2.0F;
   }

   protected String getTextureLabel() {
      return "oversized_item";
   }
}
