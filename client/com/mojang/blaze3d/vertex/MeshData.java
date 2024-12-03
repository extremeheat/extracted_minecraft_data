package com.mojang.blaze3d.vertex;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableLong;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class MeshData implements AutoCloseable {
   private final ByteBufferBuilder.Result vertexBuffer;
   @Nullable
   private ByteBufferBuilder.Result indexBuffer;
   private final DrawState drawState;

   public MeshData(ByteBufferBuilder.Result var1, DrawState var2) {
      super();
      this.vertexBuffer = var1;
      this.drawState = var2;
   }

   private static Vector3f[] unpackQuadCentroids(ByteBuffer var0, int var1, VertexFormat var2) {
      int var3 = var2.getOffset(VertexFormatElement.POSITION);
      if (var3 == -1) {
         throw new IllegalArgumentException("Cannot identify quad centers with no position element");
      } else {
         FloatBuffer var4 = var0.asFloatBuffer();
         int var5 = var2.getVertexSize() / 4;
         int var6 = var5 * 4;
         int var7 = var1 / 4;
         Vector3f[] var8 = new Vector3f[var7];

         for(int var9 = 0; var9 < var7; ++var9) {
            int var10 = var9 * var6 + var3;
            int var11 = var10 + var5 * 2;
            float var12 = var4.get(var10 + 0);
            float var13 = var4.get(var10 + 1);
            float var14 = var4.get(var10 + 2);
            float var15 = var4.get(var11 + 0);
            float var16 = var4.get(var11 + 1);
            float var17 = var4.get(var11 + 2);
            var8[var9] = new Vector3f((var12 + var15) / 2.0F, (var13 + var16) / 2.0F, (var14 + var17) / 2.0F);
         }

         return var8;
      }
   }

   public ByteBuffer vertexBuffer() {
      return this.vertexBuffer.byteBuffer();
   }

   @Nullable
   public ByteBuffer indexBuffer() {
      return this.indexBuffer != null ? this.indexBuffer.byteBuffer() : null;
   }

   public DrawState drawState() {
      return this.drawState;
   }

   @Nullable
   public SortState sortQuads(ByteBufferBuilder var1, VertexSorting var2) {
      if (this.drawState.mode() != VertexFormat.Mode.QUADS) {
         return null;
      } else {
         Vector3f[] var3 = unpackQuadCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());
         SortState var4 = new SortState(var3, this.drawState.indexType());
         this.indexBuffer = var4.buildSortedIndexBuffer(var1, var2);
         return var4;
      }
   }

   public void close() {
      this.vertexBuffer.close();
      if (this.indexBuffer != null) {
         this.indexBuffer.close();
      }

   }

   public static record DrawState(VertexFormat format, int vertexCount, int indexCount, VertexFormat.Mode mode, VertexFormat.IndexType indexType) {
      public DrawState(VertexFormat var1, int var2, int var3, VertexFormat.Mode var4, VertexFormat.IndexType var5) {
         super();
         this.format = var1;
         this.vertexCount = var2;
         this.indexCount = var3;
         this.mode = var4;
         this.indexType = var5;
      }
   }

   public static record SortState(Vector3f[] centroids, VertexFormat.IndexType indexType) {
      public SortState(Vector3f[] var1, VertexFormat.IndexType var2) {
         super();
         this.centroids = var1;
         this.indexType = var2;
      }

      @Nullable
      public ByteBufferBuilder.Result buildSortedIndexBuffer(ByteBufferBuilder var1, VertexSorting var2) {
         int[] var3 = var2.sort(this.centroids);
         long var4 = var1.reserve(var3.length * 6 * this.indexType.bytes);
         IntConsumer var6 = this.indexWriter(var4, this.indexType);

         for(int var10 : var3) {
            var6.accept(var10 * 4 + 0);
            var6.accept(var10 * 4 + 1);
            var6.accept(var10 * 4 + 2);
            var6.accept(var10 * 4 + 2);
            var6.accept(var10 * 4 + 3);
            var6.accept(var10 * 4 + 0);
         }

         return var1.build();
      }

      private IntConsumer indexWriter(long var1, VertexFormat.IndexType var3) {
         MutableLong var4 = new MutableLong(var1);
         IntConsumer var10000;
         switch (var3) {
            case SHORT -> var10000 = (var1x) -> MemoryUtil.memPutShort(var4.getAndAdd(2L), (short)var1x);
            case INT -> var10000 = (var1x) -> MemoryUtil.memPutInt(var4.getAndAdd(4L), var1x);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}
