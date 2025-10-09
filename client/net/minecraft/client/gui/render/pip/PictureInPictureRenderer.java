package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;

public abstract class PictureInPictureRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {
   protected final MultiBufferSource.BufferSource bufferSource;
   @Nullable
   private GpuTexture texture;
   @Nullable
   private GpuTextureView textureView;
   @Nullable
   private GpuTexture depthTexture;
   @Nullable
   private GpuTextureView depthTextureView;
   private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true);

   protected PictureInPictureRenderer(MultiBufferSource.BufferSource var1) {
      super();
      this.bufferSource = var1;
   }

   public void prepare(T var1, GuiRenderState var2, int var3) {
      int var4 = (var1.x1() - var1.x0()) * var3;
      int var5 = (var1.y1() - var1.y0()) * var3;
      boolean var6 = this.texture == null || this.texture.getWidth(0) != var4 || this.texture.getHeight(0) != var5;
      if (!var6 && this.textureIsReadyToBlit(var1)) {
         this.blitTexture(var1, var2);
      } else {
         this.prepareTexturesAndProjection(var6, var4, var5);
         RenderSystem.outputColorTextureOverride = this.textureView;
         RenderSystem.outputDepthTextureOverride = this.depthTextureView;
         PoseStack var7 = new PoseStack();
         var7.translate((float)var4 / 2.0F, this.getTranslateY(var5, var3), 0.0F);
         float var8 = (float)var3 * var1.scale();
         var7.scale(var8, var8, -var8);
         this.renderToTexture(var1, var7);
         this.bufferSource.endBatch();
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.blitTexture(var1, var2);
      }
   }

   protected void blitTexture(T var1, GuiRenderState var2) {
      var2.submitBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)), var1.pose(), var1.x0(), var1.y0(), var1.x1(), var1.y1(), 0.0F, 1.0F, 1.0F, 0.0F, -1, var1.scissorArea(), (ScreenRectangle)null));
   }

   private void prepareTexturesAndProjection(boolean var1, int var2, int var3) {
      if (this.texture != null && var1) {
         this.texture.close();
         this.texture = null;
         this.textureView.close();
         this.textureView = null;
         this.depthTexture.close();
         this.depthTexture = null;
         this.depthTextureView.close();
         this.depthTextureView = null;
      }

      GpuDevice var4 = RenderSystem.getDevice();
      if (this.texture == null) {
         this.texture = var4.createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " texture"), 12, TextureFormat.RGBA8, var2, var3, 1, 1);
         this.textureView = var4.createTextureView(this.texture);
         this.depthTexture = var4.createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " depth texture"), 8, TextureFormat.DEPTH32, var2, var3, 1, 1);
         this.depthTextureView = var4.createTextureView(this.depthTexture);
      }

      var4.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
      RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer((float)var2, (float)var3), ProjectionType.ORTHOGRAPHIC);
   }

   protected boolean textureIsReadyToBlit(T var1) {
      return false;
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1;
   }

   public void close() {
      if (this.texture != null) {
         this.texture.close();
      }

      if (this.textureView != null) {
         this.textureView.close();
      }

      if (this.depthTexture != null) {
         this.depthTexture.close();
      }

      if (this.depthTextureView != null) {
         this.depthTextureView.close();
      }

      this.projectionMatrixBuffer.close();
   }

   public abstract Class<T> getRenderStateClass();

   protected abstract void renderToTexture(T var1, PoseStack var2);

   protected abstract String getTextureLabel();
}
