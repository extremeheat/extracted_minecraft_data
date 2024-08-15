package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class VertexBuffer implements AutoCloseable {
   private final VertexBuffer.Usage usage;
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

   public VertexBuffer(VertexBuffer.Usage var1) {
      super();
      this.usage = var1;
      RenderSystem.assertOnRenderThread();
      this.vertexBufferId = GlStateManager._glGenBuffers();
      this.indexBufferId = GlStateManager._glGenBuffers();
      this.arrayObjectId = GlStateManager._glGenVertexArrays();
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

      label40: {
         try {
            if (this.isInvalid()) {
               break label40;
            }

            RenderSystem.assertOnRenderThread();
            GlStateManager._glBindBuffer(34963, this.indexBufferId);
            RenderSystem.glBufferData(34963, var1.byteBuffer(), this.usage.id);
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
   private RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(MeshData.DrawState var1, @Nullable ByteBuffer var2) {
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
      RenderSystem.assertOnRenderThread();
      var3.setDefaultUniforms(this.mode, var1, var2, Minecraft.getInstance().getWindow());
      var3.apply();
      this.draw();
      var3.clear();
   }

   @Override
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

      private Usage(final int nullxx) {
         this.id = nullxx;
      }
   }
}
