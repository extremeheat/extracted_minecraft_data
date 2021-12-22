package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public class VertexBuffer implements AutoCloseable {
   private int vertextBufferId;
   private int indexBufferId;
   private VertexFormat.IndexType indexType;
   private int arrayObjectId;
   private int indexCount;
   private VertexFormat.Mode mode;
   private boolean sequentialIndices;
   private VertexFormat format;

   public VertexBuffer() {
      super();
      RenderSystem.glGenBuffers((var1) -> {
         this.vertextBufferId = var1;
      });
      RenderSystem.glGenVertexArrays((var1) -> {
         this.arrayObjectId = var1;
      });
      RenderSystem.glGenBuffers((var1) -> {
         this.indexBufferId = var1;
      });
   }

   public void bind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.vertextBufferId;
      });
      if (this.sequentialIndices) {
         RenderSystem.glBindBuffer(34963, () -> {
            RenderSystem.AutoStorageIndexBuffer var1 = RenderSystem.getSequentialBuffer(this.mode, this.indexCount);
            this.indexType = var1.type();
            return var1.name();
         });
      } else {
         RenderSystem.glBindBuffer(34963, () -> {
            return this.indexBufferId;
         });
      }

   }

   public void upload(BufferBuilder var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload_(var1);
         });
      } else {
         this.upload_(var1);
      }

   }

   public CompletableFuture<Void> uploadLater(BufferBuilder var1) {
      if (!RenderSystem.isOnRenderThread()) {
         return CompletableFuture.runAsync(() -> {
            this.upload_(var1);
         }, (var0) -> {
            Objects.requireNonNull(var0);
            RenderSystem.recordRenderCall(var0::run);
         });
      } else {
         this.upload_(var1);
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   private void upload_(BufferBuilder var1) {
      Pair var2 = var1.popNextBuffer();
      if (this.vertextBufferId != 0) {
         BufferUploader.reset();
         BufferBuilder.DrawState var3 = (BufferBuilder.DrawState)var2.getFirst();
         ByteBuffer var4 = (ByteBuffer)var2.getSecond();
         int var5 = var3.vertexBufferSize();
         this.indexCount = var3.indexCount();
         this.indexType = var3.indexType();
         this.format = var3.format();
         this.mode = var3.mode();
         this.sequentialIndices = var3.sequentialIndex();
         this.bindVertexArray();
         this.bind();
         if (!var3.indexOnly()) {
            var4.limit(var5);
            RenderSystem.glBufferData(34962, var4, 35044);
            var4.position(var5);
         }

         if (!this.sequentialIndices) {
            var4.limit(var3.bufferSize());
            RenderSystem.glBufferData(34963, var4, 35044);
            var4.position(0);
         } else {
            var4.limit(var3.bufferSize());
            var4.position(0);
         }

         unbind();
         unbindVertexArray();
      }
   }

   private void bindVertexArray() {
      RenderSystem.glBindVertexArray(() -> {
         return this.arrayObjectId;
      });
   }

   public static void unbindVertexArray() {
      RenderSystem.glBindVertexArray(() -> {
         return 0;
      });
   }

   public void draw() {
      if (this.indexCount != 0) {
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
      }
   }

   public void drawWithShader(Matrix4f var1, Matrix4f var2, ShaderInstance var3) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._drawWithShader(var1.copy(), var2.copy(), var3);
         });
      } else {
         this._drawWithShader(var1, var2, var3);
      }

   }

   public void _drawWithShader(Matrix4f var1, Matrix4f var2, ShaderInstance var3) {
      if (this.indexCount != 0) {
         RenderSystem.assertOnRenderThread();
         BufferUploader.reset();

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

         if (var3.INVERSE_VIEW_ROTATION_MATRIX != null) {
            var3.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
         }

         if (var3.COLOR_MODULATOR != null) {
            var3.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
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
         this.bindVertexArray();
         this.bind();
         this.getFormat().setupBufferState();
         var3.apply();
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
         var3.clear();
         this.getFormat().clearBufferState();
         unbind();
         unbindVertexArray();
      }
   }

   public void drawChunkLayer() {
      if (this.indexCount != 0) {
         RenderSystem.assertOnRenderThread();
         this.bindVertexArray();
         this.bind();
         this.format.setupBufferState();
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
      }
   }

   public static void unbind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
      RenderSystem.glBindBuffer(34963, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.indexBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.indexBufferId);
         this.indexBufferId = -1;
      }

      if (this.vertextBufferId > 0) {
         RenderSystem.glDeleteBuffers(this.vertextBufferId);
         this.vertextBufferId = 0;
      }

      if (this.arrayObjectId > 0) {
         RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
         this.arrayObjectId = 0;
      }

   }

   public VertexFormat getFormat() {
      return this.format;
   }
}
