package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class VertexBuffer implements AutoCloseable {
   private final Usage usage;
   private int vertexBufferId;
   private int indexBufferId;
   private int arrayObjectId;
   @Nullable
   private VertexFormat format;
   @Nullable
   private RenderSystem.AutoStorageIndexBuffer sequentialIndices;
   private VertexFormat.IndexType indexType;
   private int indexCount;
   private VertexFormat.Mode mode;

   public VertexBuffer(Usage var1) {
      super();
      this.usage = var1;
      RenderSystem.assertOnRenderThread();
      this.vertexBufferId = GlStateManager._glGenBuffers();
      this.indexBufferId = GlStateManager._glGenBuffers();
      this.arrayObjectId = GlStateManager._glGenVertexArrays();
   }

   public void upload(BufferBuilder.RenderedBuffer var1) {
      try {
         if (!this.isInvalid()) {
            RenderSystem.assertOnRenderThread();
            BufferBuilder.DrawState var2 = var1.drawState();
            this.format = this.uploadVertexBuffer(var2, var1.vertexBuffer());
            this.sequentialIndices = this.uploadIndexBuffer(var2, var1.indexBuffer());
            this.indexCount = var2.indexCount();
            this.indexType = var2.indexType();
            this.mode = var2.mode();
            return;
         }
      } finally {
         var1.release();
      }

   }

   private VertexFormat uploadVertexBuffer(BufferBuilder.DrawState var1, @Nullable ByteBuffer var2) {
      boolean var3 = false;
      if (!var1.format().equals(this.format)) {
         if (this.format != null) {
            this.format.clearBufferState();
         }

         GlStateManager._glBindBuffer(34962, this.vertexBufferId);
         var1.format().setupBufferState();
         var3 = true;
      }

      if (var2 != null) {
         if (!var3) {
            GlStateManager._glBindBuffer(34962, this.vertexBufferId);
         }

         RenderSystem.glBufferData(34962, var2, this.usage.id);
      }

      return var1.format();
   }

   @Nullable
   private RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(BufferBuilder.DrawState var1, @Nullable ByteBuffer var2) {
      if (var2 != null) {
         GlStateManager._glBindBuffer(34963, this.indexBufferId);
         RenderSystem.glBufferData(34963, var2, this.usage.id);
         return null;
      } else {
         RenderSystem.AutoStorageIndexBuffer var3 = RenderSystem.getSequentialBuffer(var1.mode());
         if (var3 != this.sequentialIndices || !var3.hasStorage(var1.indexCount())) {
            var3.bind(var1.indexCount());
         }

         return var3;
      }
   }

   public void bind() {
      BufferUploader.invalidate();
      GlStateManager._glBindVertexArray(this.arrayObjectId);
   }

   public static void unbind() {
      BufferUploader.invalidate();
      GlStateManager._glBindVertexArray(0);
   }

   public void draw() {
      RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType);
   }

   private VertexFormat.IndexType getIndexType() {
      RenderSystem.AutoStorageIndexBuffer var1 = this.sequentialIndices;
      return var1 != null ? var1.type() : this.indexType;
   }

   public void drawWithShader(Matrix4f var1, Matrix4f var2, ShaderInstance var3) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._drawWithShader(new Matrix4f(var1), new Matrix4f(var2), var3);
         });
      } else {
         this._drawWithShader(var1, var2, var3);
      }

   }

   private void _drawWithShader(Matrix4f var1, Matrix4f var2, ShaderInstance var3) {
      for(int var4 = 0; var4 < 12; ++var4) {
         int var5 = RenderSystem.getShaderTexture(var4);
         var3.setSampler("Sampler" + var4, var5);
      }

      if (var3.MODEL_VIEW_MATRIX != null) {
         var3.MODEL_VIEW_MATRIX.set(var1);
      }

      if (var3.PROJECTION_MATRIX != null) {
         var3.PROJECTION_MATRIX.set(var2);
      }

      if (var3.COLOR_MODULATOR != null) {
         var3.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
      }

      if (var3.GLINT_ALPHA != null) {
         var3.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
      }

      if (var3.FOG_START != null) {
         var3.FOG_START.set(RenderSystem.getShaderFogStart());
      }

      if (var3.FOG_END != null) {
         var3.FOG_END.set(RenderSystem.getShaderFogEnd());
      }

      if (var3.FOG_COLOR != null) {
         var3.FOG_COLOR.set(RenderSystem.getShaderFogColor());
      }

      if (var3.FOG_SHAPE != null) {
         var3.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
      }

      if (var3.TEXTURE_MATRIX != null) {
         var3.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
      }

      if (var3.GAME_TIME != null) {
         var3.GAME_TIME.set(RenderSystem.getShaderGameTime());
      }

      if (var3.SCREEN_SIZE != null) {
         Window var6 = Minecraft.getInstance().getWindow();
         var3.SCREEN_SIZE.set((float)var6.getWidth(), (float)var6.getHeight());
      }

      if (var3.LINE_WIDTH != null && (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP)) {
         var3.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
      }

      RenderSystem.setupShaderLights(var3);
      var3.apply();
      this.draw();
      var3.clear();
   }

   public void close() {
      if (this.vertexBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.vertexBufferId);
         this.vertexBufferId = -1;
      }

      if (this.indexBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.indexBufferId);
         this.indexBufferId = -1;
      }

      if (this.arrayObjectId >= 0) {
         RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
         this.arrayObjectId = -1;
      }

   }

   public VertexFormat getFormat() {
      return this.format;
   }

   public boolean isInvalid() {
      return this.arrayObjectId == -1;
   }

   public static enum Usage {
      STATIC(35044),
      DYNAMIC(35048);

      final int id;

      private Usage(final int var3) {
         this.id = var3;
      }

      // $FF: synthetic method
      private static Usage[] $values() {
         return new Usage[]{STATIC, DYNAMIC};
      }
   }
}
