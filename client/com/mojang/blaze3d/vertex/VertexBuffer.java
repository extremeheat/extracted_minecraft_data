package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class VertexBuffer implements AutoCloseable {
   private final BufferUsage usage;
   private final GpuBuffer vertexBuffer;
   @Nullable
   private GpuBuffer indexBuffer = null;
   private int arrayObjectId;
   @Nullable
   private VertexFormat format;
   @Nullable
   private RenderSystem.AutoStorageIndexBuffer sequentialIndices;
   private VertexFormat.IndexType indexType;
   private int indexCount;
   private VertexFormat.Mode mode;

   public VertexBuffer(BufferUsage var1) {
      super();
      this.usage = var1;
      RenderSystem.assertOnRenderThread();
      this.vertexBuffer = new GpuBuffer(BufferType.VERTICES, var1, 0);
      this.arrayObjectId = GlStateManager._glGenVertexArrays();
   }

   public static VertexBuffer uploadStatic(VertexFormat.Mode var0, VertexFormat var1, Consumer<VertexConsumer> var2) {
      BufferBuilder var3 = Tesselator.getInstance().begin(var0, var1);
      var2.accept(var3);
      VertexBuffer var4 = new VertexBuffer(BufferUsage.STATIC_WRITE);
      var4.bind();
      var4.upload(var3.buildOrThrow());
      unbind();
      return var4;
   }

   public void upload(MeshData var1) {
      MeshData var2 = var1;

      label40: {
         try {
            if (this.isInvalid()) {
               break label40;
            }

            RenderSystem.assertOnRenderThread();
            MeshData.DrawState var3 = var1.drawState();
            this.format = this.uploadVertexBuffer(var3, var1.vertexBuffer());
            this.sequentialIndices = this.uploadIndexBuffer(var3, var1.indexBuffer());
            this.indexCount = var3.indexCount();
            this.indexType = var3.indexType();
            this.mode = var3.mode();
         } catch (Throwable var6) {
            if (var1 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var1 != null) {
            var1.close();
         }

         return;
      }

      if (var1 != null) {
         var1.close();
      }

   }

   public void uploadIndexBuffer(ByteBufferBuilder.Result var1) {
      ByteBufferBuilder.Result var2 = var1;

      label45: {
         try {
            if (this.isInvalid()) {
               break label45;
            }

            RenderSystem.assertOnRenderThread();
            if (this.indexBuffer != null) {
               this.indexBuffer.close();
            }

            this.indexBuffer = new GpuBuffer(BufferType.INDICES, this.usage, var1.byteBuffer());
            this.sequentialIndices = null;
         } catch (Throwable var6) {
            if (var1 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var1 != null) {
            var1.close();
         }

         return;
      }

      if (var1 != null) {
         var1.close();
      }

   }

   private VertexFormat uploadVertexBuffer(MeshData.DrawState var1, @Nullable ByteBuffer var2) {
      boolean var3 = false;
      if (!var1.format().equals(this.format)) {
         if (this.format != null) {
            this.format.clearBufferState();
         }

         this.vertexBuffer.bind();
         var1.format().setupBufferState();
         var3 = true;
      }

      if (var2 != null) {
         if (!var3) {
            this.vertexBuffer.bind();
         }

         this.vertexBuffer.resize(var2.remaining());
         this.vertexBuffer.write(var2, 0);
      }

      return var1.format();
   }

   @Nullable
   private RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(MeshData.DrawState var1, @Nullable ByteBuffer var2) {
      if (var2 != null) {
         if (this.indexBuffer != null) {
            this.indexBuffer.close();
         }

         this.indexBuffer = new GpuBuffer(BufferType.INDICES, this.usage, var2);
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

   public void drawWithShader(Matrix4f var1, Matrix4f var2, @Nullable CompiledShaderProgram var3) {
      if (var3 != null) {
         RenderSystem.assertOnRenderThread();
         var3.setDefaultUniforms(this.mode, var1, var2, Minecraft.getInstance().getWindow());
         var3.apply();
         this.draw();
         var3.clear();
      }
   }

   public void drawWithRenderType(RenderType var1) {
      var1.setupRenderState();
      this.bind();
      this.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
      unbind();
      var1.clearRenderState();
   }

   public void close() {
      this.vertexBuffer.close();
      if (this.indexBuffer != null) {
         this.indexBuffer.close();
         this.indexBuffer = null;
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
}
