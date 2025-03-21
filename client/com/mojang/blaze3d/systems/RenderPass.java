package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.joml.Matrix4f;

@DontObfuscate
public interface RenderPass extends AutoCloseable {
   void setPipeline(RenderPipeline var1);

   void bindSampler(String var1, GpuTexture var2);

   void setUniform(String var1, int... var2);

   void setUniform(String var1, float... var2);

   void setUniform(String var1, Matrix4f var2);

   void enableScissor(ScissorState var1);

   void enableScissor(int var1, int var2, int var3, int var4);

   void disableScissor();

   void setVertexBuffer(int var1, GpuBuffer var2);

   void setIndexBuffer(GpuBuffer var1, VertexFormat.IndexType var2);

   void drawIndexed(int var1, int var2);

   void drawMultipleIndexed(Collection<Draw> var1, @Nullable GpuBuffer var2, @Nullable VertexFormat.IndexType var3);

   void draw(int var1, int var2);

   void close();

   public static record Draw(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, @Nullable VertexFormat.IndexType indexType, int firstIndex, int indexCount, @Nullable Consumer<UniformUploader> uniformUploaderConsumer) {
      public Draw(int var1, GpuBuffer var2, GpuBuffer var3, VertexFormat.IndexType var4, int var5, int var6) {
         this(var1, var2, var3, var4, var5, var6, (Consumer)null);
      }

      public Draw(int var1, GpuBuffer var2, @Nullable GpuBuffer var3, @Nullable VertexFormat.IndexType var4, int var5, int var6, @Nullable Consumer<UniformUploader> var7) {
         super();
         this.slot = var1;
         this.vertexBuffer = var2;
         this.indexBuffer = var3;
         this.indexType = var4;
         this.firstIndex = var5;
         this.indexCount = var6;
         this.uniformUploaderConsumer = var7;
      }
   }

   public interface UniformUploader {
      void upload(String var1, float... var2);
   }
}
