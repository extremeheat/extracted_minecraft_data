package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class BufferBuilder extends DefaultedVertexConsumer implements BufferVertexConsumer {
   private static final int MAX_GROWTH_SIZE = 2097152;
   private static final Logger LOGGER = LogUtils.getLogger();
   private ByteBuffer buffer;
   private boolean closed;
   private int renderedBufferCount;
   private int renderedBufferPointer;
   private int nextElementByte;
   private int vertices;
   @Nullable
   private VertexFormatElement currentElement;
   private int elementIndex;
   private VertexFormat format;
   private VertexFormat.Mode mode;
   private boolean fastFormat;
   private boolean fullFormat;
   private boolean building;
   @Nullable
   private Vector3f[] sortingPoints;
   @Nullable
   private VertexSorting sorting;
   private boolean indexOnly;

   public BufferBuilder(int var1) {
      super();
      this.buffer = MemoryTracker.create(var1);
   }

   private void ensureVertexCapacity() {
      this.ensureCapacity(this.format.getVertexSize());
   }

   private void ensureCapacity(int var1) {
      if (this.nextElementByte + var1 > this.buffer.capacity()) {
         int var2 = this.buffer.capacity();
         int var3 = Math.min(var2, 2097152);
         int var4 = var2 + var1;
         int var5 = Math.max(var2 + var3, var4);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", var2, var5);
         ByteBuffer var6 = MemoryTracker.resize(this.buffer, var5);
         var6.rewind();
         this.buffer = var6;
      }
   }

   public void setQuadSorting(VertexSorting var1) {
      if (this.mode == VertexFormat.Mode.QUADS) {
         this.sorting = var1;
         if (this.sortingPoints == null) {
            this.sortingPoints = this.makeQuadSortingPoints();
         }
      }
   }

   public BufferBuilder.SortState getSortState() {
      return new BufferBuilder.SortState(this.mode, this.vertices, this.sortingPoints, this.sorting);
   }

   private void checkOpen() {
      if (this.closed) {
         throw new IllegalStateException("This BufferBuilder has been closed");
      }
   }

   public void restoreSortState(BufferBuilder.SortState var1) {
      this.checkOpen();
      this.buffer.rewind();
      this.mode = var1.mode;
      this.vertices = var1.vertices;
      this.nextElementByte = this.renderedBufferPointer;
      this.sortingPoints = var1.sortingPoints;
      this.sorting = var1.sorting;
      this.indexOnly = true;
   }

   public void begin(VertexFormat.Mode var1, VertexFormat var2) {
      if (this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.checkOpen();
         this.building = true;
         this.mode = var1;
         this.switchFormat(var2);
         this.currentElement = (VertexFormatElement)var2.getElements().get(0);
         this.elementIndex = 0;
         this.buffer.rewind();
      }
   }

   private void switchFormat(VertexFormat var1) {
      if (this.format != var1) {
         this.format = var1;
         boolean var2 = var1 == DefaultVertexFormat.NEW_ENTITY;
         boolean var3 = var1 == DefaultVertexFormat.BLOCK;
         this.fastFormat = var2 || var3;
         this.fullFormat = var2;
      }
   }

   private IntConsumer intConsumer(int var1, VertexFormat.IndexType var2) {
      MutableInt var3 = new MutableInt(var1);

      return switch (var2) {
         case SHORT -> var2x -> this.buffer.putShort(var3.getAndAdd(2), (short)var2x);
         case INT -> var2x -> this.buffer.putInt(var3.getAndAdd(4), var2x);
      };
   }

   private Vector3f[] makeQuadSortingPoints() {
      FloatBuffer var1 = this.buffer.asFloatBuffer();
      int var2 = this.renderedBufferPointer / 4;
      int var3 = this.format.getIntegerSize();
      int var4 = var3 * this.mode.primitiveStride;
      int var5 = this.vertices / this.mode.primitiveStride;
      Vector3f[] var6 = new Vector3f[var5];

      for (int var7 = 0; var7 < var5; var7++) {
         float var8 = var1.get(var2 + var7 * var4 + 0);
         float var9 = var1.get(var2 + var7 * var4 + 1);
         float var10 = var1.get(var2 + var7 * var4 + 2);
         float var11 = var1.get(var2 + var7 * var4 + var3 * 2 + 0);
         float var12 = var1.get(var2 + var7 * var4 + var3 * 2 + 1);
         float var13 = var1.get(var2 + var7 * var4 + var3 * 2 + 2);
         float var14 = (var8 + var11) / 2.0F;
         float var15 = (var9 + var12) / 2.0F;
         float var16 = (var10 + var13) / 2.0F;
         var6[var7] = new Vector3f(var14, var15, var16);
      }

      return var6;
   }

   private void putSortedQuadIndices(VertexFormat.IndexType var1) {
      if (this.sortingPoints != null && this.sorting != null) {
         int[] var2 = this.sorting.sort(this.sortingPoints);
         IntConsumer var3 = this.intConsumer(this.nextElementByte, var1);

         for (int var7 : var2) {
            var3.accept(var7 * this.mode.primitiveStride + 0);
            var3.accept(var7 * this.mode.primitiveStride + 1);
            var3.accept(var7 * this.mode.primitiveStride + 2);
            var3.accept(var7 * this.mode.primitiveStride + 2);
            var3.accept(var7 * this.mode.primitiveStride + 3);
            var3.accept(var7 * this.mode.primitiveStride + 0);
         }
      } else {
         throw new IllegalStateException("Sorting state uninitialized");
      }
   }

   public boolean isCurrentBatchEmpty() {
      return this.vertices == 0;
   }

   @Nullable
   public BufferBuilder.RenderedBuffer endOrDiscardIfEmpty() {
      this.ensureDrawing();
      if (this.isCurrentBatchEmpty()) {
         this.reset();
         return null;
      } else {
         BufferBuilder.RenderedBuffer var1 = this.storeRenderedBuffer();
         this.reset();
         return var1;
      }
   }

   public BufferBuilder.RenderedBuffer end() {
      this.ensureDrawing();
      BufferBuilder.RenderedBuffer var1 = this.storeRenderedBuffer();
      this.reset();
      return var1;
   }

   private void ensureDrawing() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      }
   }

   private BufferBuilder.RenderedBuffer storeRenderedBuffer() {
      int var1 = this.mode.indexCount(this.vertices);
      int var2 = !this.indexOnly ? this.vertices * this.format.getVertexSize() : 0;
      VertexFormat.IndexType var3 = VertexFormat.IndexType.least(this.vertices);
      boolean var4;
      int var5;
      if (this.sortingPoints != null) {
         int var6 = Mth.roundToward(var1 * var3.bytes, 4);
         this.ensureCapacity(var6);
         this.putSortedQuadIndices(var3);
         var4 = false;
         this.nextElementByte += var6;
         var5 = var2 + var6;
      } else {
         var4 = true;
         var5 = var2;
      }

      int var8 = this.renderedBufferPointer;
      this.renderedBufferPointer += var5;
      this.renderedBufferCount++;
      BufferBuilder.DrawState var7 = new BufferBuilder.DrawState(this.format, this.vertices, var1, this.mode, var3, this.indexOnly, var4);
      return new BufferBuilder.RenderedBuffer(var8, var7);
   }

   private void reset() {
      this.building = false;
      this.vertices = 0;
      this.currentElement = null;
      this.elementIndex = 0;
      this.sortingPoints = null;
      this.sorting = null;
      this.indexOnly = false;
   }

   @Override
   public void putByte(int var1, byte var2) {
      this.buffer.put(this.nextElementByte + var1, var2);
   }

   @Override
   public void putShort(int var1, short var2) {
      this.buffer.putShort(this.nextElementByte + var1, var2);
   }

   @Override
   public void putFloat(int var1, float var2) {
      this.buffer.putFloat(this.nextElementByte + var1, var2);
   }

   @Override
   public void endVertex() {
      if (this.elementIndex != 0) {
         throw new IllegalStateException("Not filled all elements of the vertex");
      } else {
         this.vertices++;
         this.ensureVertexCapacity();
         if (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP) {
            int var1 = this.format.getVertexSize();
            this.buffer.put(this.nextElementByte, this.buffer, this.nextElementByte - var1, var1);
            this.nextElementByte += var1;
            this.vertices++;
            this.ensureVertexCapacity();
         }
      }
   }

   @Override
   public void nextElement() {
      ImmutableList var1 = this.format.getElements();
      this.elementIndex = (this.elementIndex + 1) % var1.size();
      this.nextElementByte = this.nextElementByte + this.currentElement.getByteSize();
      VertexFormatElement var2 = (VertexFormatElement)var1.get(this.elementIndex);
      this.currentElement = var2;
      if (var2.getUsage() == VertexFormatElement.Usage.PADDING) {
         this.nextElement();
      }

      if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
         BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
      }
   }

   @Override
   public VertexConsumer color(int var1, int var2, int var3, int var4) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else {
         return BufferVertexConsumer.super.color(var1, var2, var3, var4);
      }
   }

   @Override
   public void vertex(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      float var14
   ) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else if (this.fastFormat) {
         this.putFloat(0, var1);
         this.putFloat(4, var2);
         this.putFloat(8, var3);
         this.putByte(12, (byte)((int)(var4 * 255.0F)));
         this.putByte(13, (byte)((int)(var5 * 255.0F)));
         this.putByte(14, (byte)((int)(var6 * 255.0F)));
         this.putByte(15, (byte)((int)(var7 * 255.0F)));
         this.putFloat(16, var8);
         this.putFloat(20, var9);
         byte var15;
         if (this.fullFormat) {
            this.putShort(24, (short)(var10 & 65535));
            this.putShort(26, (short)(var10 >> 16 & 65535));
            var15 = 28;
         } else {
            var15 = 24;
         }

         this.putShort(var15 + 0, (short)(var11 & 65535));
         this.putShort(var15 + 2, (short)(var11 >> 16 & 65535));
         this.putByte(var15 + 4, BufferVertexConsumer.normalIntValue(var12));
         this.putByte(var15 + 5, BufferVertexConsumer.normalIntValue(var13));
         this.putByte(var15 + 6, BufferVertexConsumer.normalIntValue(var14));
         this.nextElementByte += var15 + 8;
         this.endVertex();
      } else {
         super.vertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }
   }

   void releaseRenderedBuffer() {
      if (this.renderedBufferCount > 0 && --this.renderedBufferCount == 0) {
         this.clear();
      }
   }

   public void clear() {
      if (this.renderedBufferCount > 0) {
         LOGGER.warn("Clearing BufferBuilder with unused batches");
      }

      this.discard();
   }

   public void discard() {
      this.renderedBufferCount = 0;
      this.renderedBufferPointer = 0;
      this.nextElementByte = 0;
   }

   public void release() {
      if (this.renderedBufferCount > 0) {
         throw new IllegalStateException("BufferBuilder closed with unused batches");
      } else if (this.building) {
         throw new IllegalStateException("Cannot close BufferBuilder while it is building");
      } else if (!this.closed) {
         this.closed = true;
         MemoryTracker.free(this.buffer);
      }
   }

   @Override
   public VertexFormatElement currentElement() {
      if (this.currentElement == null) {
         throw new IllegalStateException("BufferBuilder not started");
      } else {
         return this.currentElement;
      }
   }

   public boolean building() {
      return this.building;
   }

   ByteBuffer bufferSlice(int var1, int var2) {
      return MemoryUtil.memSlice(this.buffer, var1, var2 - var1);
   }

   public static record DrawState(
      VertexFormat format,
      int vertexCount,
      int indexCount,
      VertexFormat.Mode mode,
      VertexFormat.IndexType indexType,
      boolean indexOnly,
      boolean sequentialIndex
   ) {

      public DrawState(
         VertexFormat format,
         int vertexCount,
         int indexCount,
         VertexFormat.Mode mode,
         VertexFormat.IndexType indexType,
         boolean indexOnly,
         boolean sequentialIndex
      ) {
         super();
         this.format = format;
         this.vertexCount = vertexCount;
         this.indexCount = indexCount;
         this.mode = mode;
         this.indexType = indexType;
         this.indexOnly = indexOnly;
         this.sequentialIndex = sequentialIndex;
      }

      public int vertexBufferSize() {
         return this.vertexCount * this.format.getVertexSize();
      }

      public int vertexBufferStart() {
         return 0;
      }

      public int vertexBufferEnd() {
         return this.vertexBufferSize();
      }

      public int indexBufferStart() {
         return this.indexOnly ? 0 : this.vertexBufferEnd();
      }

      public int indexBufferEnd() {
         return this.indexBufferStart() + this.indexBufferSize();
      }

      private int indexBufferSize() {
         return this.sequentialIndex ? 0 : this.indexCount * this.indexType.bytes;
      }

      public int bufferSize() {
         return this.indexBufferEnd();
      }
   }

   public class RenderedBuffer {
      private final int pointer;
      private final BufferBuilder.DrawState drawState;
      private boolean released;

      RenderedBuffer(int var2, BufferBuilder.DrawState var3) {
         super();
         this.pointer = var2;
         this.drawState = var3;
      }

      @Nullable
      public ByteBuffer vertexBuffer() {
         if (this.drawState.indexOnly()) {
            return null;
         } else {
            int var1 = this.pointer + this.drawState.vertexBufferStart();
            int var2 = this.pointer + this.drawState.vertexBufferEnd();
            return BufferBuilder.this.bufferSlice(var1, var2);
         }
      }

      @Nullable
      public ByteBuffer indexBuffer() {
         if (this.drawState.sequentialIndex()) {
            return null;
         } else {
            int var1 = this.pointer + this.drawState.indexBufferStart();
            int var2 = this.pointer + this.drawState.indexBufferEnd();
            return BufferBuilder.this.bufferSlice(var1, var2);
         }
      }

      public BufferBuilder.DrawState drawState() {
         return this.drawState;
      }

      public boolean isEmpty() {
         return this.drawState.vertexCount == 0;
      }

      public void release() {
         if (this.released) {
            throw new IllegalStateException("Buffer has already been released!");
         } else {
            BufferBuilder.this.releaseRenderedBuffer();
            this.released = true;
         }
      }
   }

   public static class SortState {
      final VertexFormat.Mode mode;
      final int vertices;
      @Nullable
      final Vector3f[] sortingPoints;
      @Nullable
      final VertexSorting sorting;

      SortState(VertexFormat.Mode var1, int var2, @Nullable Vector3f[] var3, @Nullable VertexSorting var4) {
         super();
         this.mode = var1;
         this.vertices = var2;
         this.sortingPoints = var3;
         this.sorting = var4;
      }
   }
}
