package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.util.Objects;
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

   public static void decodeQuadCentroids(final ByteBuffer vertexBuffer, final int vertexCount, final VertexFormat format, final CompactVectorArray output, final int outputIndex) {
      VertexFormatElement positionElement = format.getElement("Position");
      if (positionElement == null) {
         throw new IllegalArgumentException("Cannot identify quad centers with no position element");
      } else {
         int positionOffset = vertexBuffer.position() + positionElement.offset();
         int vertexStride = format.getVertexSize();
         int quadStride = vertexStride * 4;
         int quadCount = vertexCount / 4;

         for(int i = 0; i < quadCount; ++i) {
            int firstPosOffset = i * quadStride + positionOffset;
            int secondPosOffset = firstPosOffset + vertexStride * 2;
            float x0 = vertexBuffer.getFloat(firstPosOffset + 0);
            float y0 = vertexBuffer.getFloat(firstPosOffset + 4);
            float z0 = vertexBuffer.getFloat(firstPosOffset + 8);
            float x1 = vertexBuffer.getFloat(secondPosOffset + 0);
            float y1 = vertexBuffer.getFloat(secondPosOffset + 4);
            float z1 = vertexBuffer.getFloat(secondPosOffset + 8);
            float xMid = (x0 + x1) / 2.0F;
            float yMid = (y0 + y1) / 2.0F;
            float zMid = (z0 + z1) / 2.0F;
            output.set(outputIndex + i, xMid, yMid, zMid);
         }

      }
   }

   public ByteBuffer vertexBuffer() {
      return this.vertexBuffer.byteBuffer();
   }

   public @Nullable ByteBuffer indexBuffer() {
      return this.indexBuffer != null ? this.indexBuffer.byteBuffer() : null;
   }

   public ByteBufferBuilder.Result vertexBufferSlice() {
      return this.vertexBuffer;
   }

   public DrawState drawState() {
      return this.drawState;
   }

   public @Nullable SortState sortQuads(final ByteBufferBuilder indexBufferTarget, final VertexSorting sorting) {
      if (this.drawState.primitiveTopology() != PrimitiveTopology.QUADS) {
         return null;
      } else {
         CompactVectorArray centroids = new CompactVectorArray(this.drawState.vertexCount() / 4);
         decodeQuadCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format(), centroids, 0);
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

   public static record DrawState(VertexFormat format, int vertexCount, int indexCount, PrimitiveTopology primitiveTopology, IndexType indexType) {
      public DrawState {
         super();
      }
   }

   public static record SortState(CompactVectorArray centroids, IndexType indexType) {
      public SortState {
         super();
      }

      public ByteBufferBuilder.Result buildSortedIndexBuffer(final ByteBufferBuilder target, final VertexSorting sorting) {
         int[] startIndices = sorting.sort(this.centroids);
         long pointer = target.reserve(startIndices.length * 6 * this.indexType.bytes);
         writeIndices(startIndices, this.indexWriter(pointer, this.indexType));
         return target.build();
      }

      public void writeSortedIndexBuffer(final ByteBuffer target, final VertexSorting sorting) {
         IntConsumer var10000;
         switch (this.indexType) {
            case SHORT:
               var10000 = (value) -> target.putShort((short)value);
               break;
            case INT:
               Objects.requireNonNull(target);
               var10000 = target::putInt;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         IntConsumer output = var10000;
         writeIndices(sorting.sort(this.centroids), output);
      }

      private static void writeIndices(final int[] startIndices, final IntConsumer output) {
         for(int startIndex : startIndices) {
            output.accept(startIndex * 4 + 0);
            output.accept(startIndex * 4 + 1);
            output.accept(startIndex * 4 + 2);
            output.accept(startIndex * 4 + 2);
            output.accept(startIndex * 4 + 3);
            output.accept(startIndex * 4 + 0);
         }

      }

      private IntConsumer indexWriter(final long pointer, final IndexType indexType) {
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
