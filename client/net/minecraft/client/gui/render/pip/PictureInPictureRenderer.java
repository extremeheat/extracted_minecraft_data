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
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import org.jspecify.annotations.Nullable;

public abstract class PictureInPictureRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {
   protected final MultiBufferSource.BufferSource bufferSource;
   private @Nullable GpuTexture texture;
   private @Nullable GpuTextureView textureView;
   private @Nullable GpuTexture depthTexture;
   private @Nullable GpuTextureView depthTextureView;
   private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true);

   protected PictureInPictureRenderer(final MultiBufferSource.BufferSource bufferSource) {
      super();
      this.bufferSource = bufferSource;
   }

   public void prepare(final T renderState, final GuiRenderState guiRenderState, final int guiScale) {
      int width = (renderState.x1() - renderState.x0()) * guiScale;
      int height = (renderState.y1() - renderState.y0()) * guiScale;
      boolean needsAResize = this.texture == null || this.texture.getWidth(0) != width || this.texture.getHeight(0) != height;
      if (!needsAResize && this.textureIsReadyToBlit(renderState)) {
         this.blitTexture(renderState, guiRenderState);
      } else {
         this.prepareTexturesAndProjection(needsAResize, width, height);
         RenderSystem.outputColorTextureOverride = this.textureView;
         RenderSystem.outputDepthTextureOverride = this.depthTextureView;
         PoseStack poseStack = new PoseStack();
         poseStack.translate((float)width / 2.0F, this.getTranslateY(height, guiScale), 0.0F);
         float scale = (float)guiScale * renderState.scale();
         poseStack.scale(scale, scale, -scale);
         this.renderToTexture(renderState, poseStack);
         this.bufferSource.endBatch();
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.blitTexture(renderState, guiRenderState);
      }
   }

   protected void blitTexture(final T renderState, final GuiRenderState guiRenderState) {
      guiRenderState.submitBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)), renderState.pose(), renderState.x0(), renderState.y0(), renderState.x1(), renderState.y1(), 0.0F, 1.0F, 1.0F, 0.0F, -1, renderState.scissorArea(), (ScreenRectangle)null));
   }

   private void prepareTexturesAndProjection(final boolean needsAResize, final int width, final int height) {
      if (this.texture != null && needsAResize) {
         this.texture.close();
         this.texture = null;
         this.textureView.close();
         this.textureView = null;
         this.depthTexture.close();
         this.depthTexture = null;
         this.depthTextureView.close();
         this.depthTextureView = null;
      }

      GpuDevice device = RenderSystem.getDevice();
      if (this.texture == null) {
         this.texture = device.createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " texture"), 13, TextureFormat.RGBA8, width, height, 1, 1);
         this.textureView = device.createTextureView(this.texture);
         this.depthTexture = device.createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " depth texture"), 9, TextureFormat.DEPTH32, width, height, 1, 1);
         this.depthTextureView = device.createTextureView(this.depthTexture);
      }

      device.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
      RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer((float)width, (float)height), ProjectionType.ORTHOGRAPHIC);
   }

   protected boolean textureIsReadyToBlit(final T renderState) {
      return false;
   }

   protected float getTranslateY(final int height, final int guiScale) {
      return (float)height;
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

   protected abstract void renderToTexture(final T renderState, final PoseStack poseStack);

   protected abstract String getTextureLabel();
}
