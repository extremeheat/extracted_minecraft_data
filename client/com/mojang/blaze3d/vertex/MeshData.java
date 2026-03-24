package com.mojang.blaze3d.vertex;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.apache.commons.lang3.mutable.MutableLong;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class MeshData implements AutoCloseable {
   private final ByteBufferBuilder.Result vertexBuffer;
   private ByteBufferBuilder.Result indexBuffer;
   private final DrawState drawState;

   public MeshData(final ByteBufferBuilder.Result vertexBuffer, final DrawState drawState) {
      super();
      this.vertexBuffer = vertexBuffer;
      this.drawState = drawState;
   }

   private static CompactVectorArray unpackQuadCentroids(final ByteBuffer vertexBuffer, final int vertices, final VertexFormat format) {
      int positionOffset = format.getOffset(VertexFormatElement.POSITION);
      if (positionOffset == -1) {
         throw new IllegalArgumentException("Cannot identify quad centers with no position element");
      } else {
         FloatBuffer floatBuffer = vertexBuffer.asFloatBuffer();
         int vertexStride = format.getVertexSize() / 4;
         int quadStride = vertexStride * 4;
         int quads = vertices / 4;
         CompactVectorArray sortingPoints = new CompactVectorArray(quads);

         for(int i = 0; i < quads; ++i) {
            int firstPosOffset = i * quadStride + positionOffset;
            int secondPosOffset = firstPosOffset + vertexStride * 2;
            float x0 = floatBuffer.get(firstPosOffset + 0);
            float y0 = floatBuffer.get(firstPosOffset + 1);
            float z0 = floatBuffer.get(firstPosOffset + 2);
            float x1 = floatBuffer.get(secondPosOffset + 0);
            float y1 = floatBuffer.get(secondPosOffset + 1);
            float z1 = floatBuffer.get(secondPosOffset + 2);
            float xMid = (x0 + x1) / 2.0F;
            float yMid = (y0 + y1) / 2.0F;
            float zMid = (z0 + z1) / 2.0F;
            sortingPoints.set(i, xMid, yMid, zMid);
         }

         return sortingPoints;
      }
   }

   public ByteBuffer vertexBuffer() {
      return this.vertexBuffer.byteBuffer();
   }

   public @Nullable ByteBuffer indexBuffer() {
      return this.indexBuffer != null ? this.indexBuffer.byteBuffer() : null;
   }

   public DrawState drawState() {
      return this.drawState;
   }

   public @Nullable SortState sortQuads(final ByteBufferBuilder indexBufferTarget, final VertexSorting sorting) {
      if (this.drawState.mode() != VertexFormat.Mode.QUADS) {
         return null;
      } else {
         CompactVectorArray centroids = unpackQuadCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());
         SortState sortState = new SortState(centroids, this.drawState.indexType());
         this.indexBuffer = sortState.buildSortedIndexBuffer(indexBufferTarget, sorting);
         return sortState;
      }
   }

   public void close() {
      this.vertexBuffer.close();
      if (this.indexBuffer != null) {
         this.indexBuffer.close();
      }

   }

   public static record DrawState(VertexFormat format, int vertexCount, int indexCount, VertexFormat.Mode mode, VertexFormat.IndexType indexType) {
      public DrawState {
         super();
      }
   }

   public static record SortState(CompactVectorArray centroids, VertexFormat.IndexType indexType) {
      public SortState {
         super();
      }

      public ByteBufferBuilder.Result buildSortedIndexBuffer(final ByteBufferBuilder target, final VertexSorting sorting) {
         int[] startIndices = sorting.sort(this.centroids);
         long pointer = target.reserve(startIndices.length * 6 * this.indexType.bytes);
         IntConsumer indexWriter = this.indexWriter(pointer, this.indexType);

         for(int startIndex : startIndices) {
            indexWriter.accept(startIndex * 4 + 0);
            indexWriter.accept(startIndex * 4 + 1);
            indexWriter.accept(startIndex * 4 + 2);
            indexWriter.accept(startIndex * 4 + 2);
            indexWriter.accept(startIndex * 4 + 3);
            indexWriter.accept(startIndex * 4 + 0);
         }

         return target.build();
      }

      private IntConsumer indexWriter(final long pointer, final VertexFormat.IndexType indexType) {
         MutableLong nextIndex = new MutableLong(pointer);
         IntConsumer var10000;
         switch (indexType) {
            case SHORT -> var10000 = (value) -> MemoryUtil.memPutShort(nextIndex.getAndAdd(2L), (short)value);
            case INT -> var10000 = (value) -> MemoryUtil.memPutInt(nextIndex.getAndAdd(4L), value);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}
