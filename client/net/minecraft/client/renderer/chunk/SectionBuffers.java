package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nullable;

public final class SectionBuffers implements AutoCloseable {
   private GpuBuffer vertexBuffer;
   @Nullable
   private GpuBuffer indexBuffer;
   private int indexCount;
   private VertexFormat.IndexType indexType;

   public SectionBuffers(GpuBuffer var1, @Nullable GpuBuffer var2, int var3, VertexFormat.IndexType var4) {
      super();
      this.vertexBuffer = var1;
      this.indexBuffer = var2;
      this.indexCount = var3;
      this.indexType = var4;
   }

   public GpuBuffer getVertexBuffer() {
      return this.vertexBuffer;
   }

   @Nullable
   public GpuBuffer getIndexBuffer() {
      return this.indexBuffer;
   }

   public void setIndexBuffer(@Nullable GpuBuffer var1) {
      this.indexBuffer = var1;
   }

   public int getIndexCount() {
      return this.indexCount;
   }

   public VertexFormat.IndexType getIndexType() {
      return this.indexType;
   }

   public void setIndexType(VertexFormat.IndexType var1) {
      this.indexType = var1;
   }

   public void setIndexCount(int var1) {
      this.indexCount = var1;
   }

   public void setVertexBuffer(GpuBuffer var1) {
      this.vertexBuffer = var1;
   }

   public void close() {
      this.vertexBuffer.close();
      if (this.indexBuffer != null) {
         this.indexBuffer.close();
      }

   }
}
