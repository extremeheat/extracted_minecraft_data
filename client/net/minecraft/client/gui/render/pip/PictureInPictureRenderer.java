package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;

public abstract class PictureInPictureRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {
   private static final Matrix3x2f IDENTITY_POSE = new Matrix3x2f();
   protected final MultiBufferSource.BufferSource bufferSource;
   @Nullable
   private GpuTexture texture;
   @Nullable
   private GpuTexture depthTexture;
   private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true);

   protected PictureInPictureRenderer(MultiBufferSource.BufferSource var1) {
      super();
      this.bufferSource = var1;
   }

   public void prepare(T var1, GuiRenderState var2, int var3) {
      int var4 = (var1.x1() - var1.x0()) * var3;
      int var5 = (var1.y1() - var1.y0()) * var3;
      this.prepareTexturesAndProjection(var4, var5);
      RenderSystem.outputColorTextureOverride = this.texture;
      RenderSystem.outputDepthTextureOverride = this.depthTexture;
      PoseStack var6 = new PoseStack();
      var6.translate((float)var4 / 2.0F, this.getTranslateY(var5, var3), 0.0F);
      float var7 = (float)var3 * var1.scale();
      var6.scale(var7, var7, -var7);
      this.renderToTexture(var1, var6);
      this.bufferSource.endBatch();
      RenderSystem.outputColorTextureOverride = null;
      RenderSystem.outputDepthTextureOverride = null;
      var2.submitGuiElement(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.texture), IDENTITY_POSE, var1.x0(), var1.y0(), var1.x1(), var1.y1(), 0.0F, 1.0F, 1.0F, 0.0F, -1, var1.scissorArea()));
   }

   private void prepareTexturesAndProjection(int var1, int var2) {
      if (this.texture != null && (this.texture.getWidth(0) != var1 || this.texture.getHeight(0) != var2)) {
         this.texture.close();
         this.texture = null;
         this.depthTexture.close();
         this.depthTexture = null;
      }

      if (this.texture == null) {
         this.texture = RenderSystem.getDevice().createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " texture"), TextureFormat.RGBA8, var1, var2, 1);
         this.texture.setTextureFilter(FilterMode.NEAREST, false);
         this.depthTexture = RenderSystem.getDevice().createTexture((Supplier)(() -> "UI " + this.getTextureLabel() + " depth texture"), TextureFormat.DEPTH32, var1, var2, 1);
      }

      RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
      RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer((float)var1, (float)var2), ProjectionType.ORTHOGRAPHIC);
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1;
   }

   public void close() {
      if (this.texture != null) {
         this.texture.close();
      }

      if (this.depthTexture != null) {
         this.depthTexture.close();
      }

      this.projectionMatrixBuffer.close();
   }

   public abstract Class<T> getRenderStateClass();

   protected abstract void renderToTexture(T var1, PoseStack var2);

   protected abstract String getTextureLabel();
}
