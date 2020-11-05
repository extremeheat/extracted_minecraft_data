package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder extends DefaultedVertexConsumer implements BufferVertexConsumer {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer buffer;
   private final List<BufferBuilder.DrawState> drawStates = Lists.newArrayList();
   private int lastPoppedStateIndex;
   private int totalRenderedBytes;
   private int nextElementByte;
   private int totalUploadedBytes;
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
   private float sortX = 0.0F / 0.0;
   private float sortY = 0.0F / 0.0;
   private float sortZ = 0.0F / 0.0;
   private boolean indexOnly;

   public BufferBuilder(int var1) {
      super();
      this.buffer = MemoryTracker.createByteBuffer(var1 * 6);
   }

   private void ensureVertexCapacity() {
      this.ensureCapacity(this.format.getVertexSize());
   }

   private void ensureCapacity(int var1) {
      if (this.nextElementByte + var1 > this.buffer.capacity()) {
         int var2 = this.buffer.capacity();
         int var3 = var2 + roundUp(var1);
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", var2, var3);
         ByteBuffer var4 = MemoryTracker.createByteBuffer(var3);
         this.buffer.position(0);
         var4.put(this.buffer);
         var4.rewind();
         this.buffer = var4;
      }
   }

   private static int roundUp(int var0) {
      int var1 = 2097152;
      if (var0 == 0) {
         return var1;
      } else {
         if (var0 < 0) {
            var1 *= -1;
         }

         int var2 = var0 % var1;
         return var2 == 0 ? var0 : var0 + var1 - var2;
      }
   }

   public void setQuadSortOrigin(float var1, float var2, float var3) {
      if (this.mode == VertexFormat.Mode.QUADS) {
         if (this.sortX != var1 || this.sortY != var2 || this.sortZ != var3) {
            this.sortX = var1;
            this.sortY = var2;
            this.sortZ = var3;
            if (this.sortingPoints == null) {
               this.sortingPoints = this.makeQuadSortingPoints();
            }
         }

      }
   }

   public BufferBuilder.SortState getSortState() {
      return new BufferBuilder.SortState(this.mode, this.vertices, this.sortingPoints, this.sortX, this.sortY, this.sortZ);
   }

   public void restoreSortState(BufferBuilder.SortState var1) {
      this.buffer.clear();
      this.mode = var1.mode;
      this.vertices = var1.vertices;
      this.nextElementByte = this.totalRenderedBytes;
      this.sortingPoints = var1.sortingPoints;
      this.sortX = var1.sortX;
      this.sortY = var1.sortY;
      this.sortZ = var1.sortZ;
      this.indexOnly = true;
   }

   public void begin(VertexFormat.Mode var1, VertexFormat var2) {
      if (this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.building = true;
         this.mode = var1;
         this.switchFormat(var2);
         this.currentElement = (VertexFormatElement)var2.getElements().get(0);
         this.elementIndex = 0;
         this.buffer.clear();
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

   private IntConsumer intConsumer(VertexFormat.IndexType var1) {
      switch(var1) {
      case BYTE:
         return (var1x) -> {
            this.buffer.put((byte)var1x);
         };
      case SHORT:
         return (var1x) -> {
            this.buffer.putShort((short)var1x);
         };
      case INT:
      default:
         return (var1x) -> {
            this.buffer.putInt(var1x);
         };
      }
   }

   private Vector3f[] makeQuadSortingPoints() {
      FloatBuffer var1 = this.buffer.asFloatBuffer();
      int var2 = this.totalRenderedBytes / 4;
      int var3 = this.format.getIntegerSize();
      int var4 = var3 * this.mode.primitiveStride;
      int var5 = this.vertices / this.mode.primitiveStride;
      Vector3f[] var6 = new Vector3f[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
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
      float[] var2 = new float[this.sortingPoints.length];
      int[] var3 = new int[this.sortingPoints.length];

      for(int var4 = 0; var4 < this.sortingPoints.length; var3[var4] = var4++) {
         float var5 = this.sortingPoints[var4].x() - this.sortX;
         float var6 = this.sortingPoints[var4].y() - this.sortY;
         float var7 = this.sortingPoints[var4].z() - this.sortZ;
         var2[var4] = var5 * var5 + var6 * var6 + var7 * var7;
      }

      IntArrays.mergeSort(var3, (var1x, var2x) -> {
         return Floats.compare(var2[var2x], var2[var1x]);
      });
      IntConsumer var9 = this.intConsumer(var1);
      this.buffer.position(this.nextElementByte);
      int[] var10 = var3;
      int var11 = var3.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         int var8 = var10[var12];
         var9.accept(var8 * this.mode.primitiveStride + 0);
         var9.accept(var8 * this.mode.primitiveStride + 1);
         var9.accept(var8 * this.mode.primitiveStride + 2);
         var9.accept(var8 * this.mode.primitiveStride + 2);
         var9.accept(var8 * this.mode.primitiveStride + 3);
         var9.accept(var8 * this.mode.primitiveStride + 0);
      }

   }

   public void end() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         int var1 = this.mode.indexCount(this.vertices);
         VertexFormat.IndexType var2 = VertexFormat.IndexType.least(var1);
         boolean var3;
         if (this.sortingPoints != null) {
            int var4 = Mth.roundToward(var1 * var2.bytes, 4);
            this.ensureCapacity(var4);
            this.putSortedQuadIndices(var2);
            var3 = false;
            this.nextElementByte += var4;
            this.totalRenderedBytes += this.vertices * this.format.getVertexSize() + var4;
         } else {
            var3 = true;
            this.totalRenderedBytes += this.vertices * this.format.getVertexSize();
         }

         this.building = false;
         this.drawStates.add(new BufferBuilder.DrawState(this.format, this.vertices, var1, this.mode, var2, this.indexOnly, var3));
         this.vertices = 0;
         this.currentElement = null;
         this.elementIndex = 0;
         this.sortingPoints = null;
         this.sortX = 0.0F / 0.0;
         this.sortY = 0.0F / 0.0;
         this.sortZ = 0.0F / 0.0;
         this.indexOnly = false;
      }
   }

   public void putByte(int var1, byte var2) {
      this.buffer.put(this.nextElementByte + var1, var2);
   }

   public void putShort(int var1, short var2) {
      this.buffer.putShort(this.nextElementByte + var1, var2);
   }

   public void putFloat(int var1, float var2) {
      this.buffer.putFloat(this.nextElementByte + var1, var2);
   }

   public void endVertex() {
      if (this.elementIndex != 0) {
         throw new IllegalStateException("Not filled all elements of the vertex");
      } else {
         ++this.vertices;
         this.ensureVertexCapacity();
      }
   }

   public void nextElement() {
      ImmutableList var1 = this.format.getElements();
      this.elementIndex = (this.elementIndex + 1) % var1.size();
      this.nextElementByte += this.currentElement.getByteSize();
      VertexFormatElement var2 = (VertexFormatElement)var1.get(this.elementIndex);
      this.currentElement = var2;
      if (var2.getUsage() == VertexFormatElement.Usage.PADDING) {
         this.nextElement();
      }

      if (this.defaultColorSet && this.currentElement.getUsage() == VertexFormatElement.Usage.COLOR) {
         BufferVertexConsumer.super.color(this.defaultR, this.defaultG, this.defaultB, this.defaultA);
      }

   }

   public VertexConsumer color(int var1, int var2, int var3, int var4) {
      if (this.defaultColorSet) {
         throw new IllegalStateException();
      } else {
         return BufferVertexConsumer.super.color(var1, var2, var3, var4);
      }
   }

   public void vertex(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11, float var12, float var13, float var14) {
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
            this.putShort(24, (short)(var10 & '\uffff'));
            this.putShort(26, (short)(var10 >> 16 & '\uffff'));
            var15 = 28;
         } else {
            var15 = 24;
         }

         this.putShort(var15 + 0, (short)(var11 & '\uffff'));
         this.putShort(var15 + 2, (short)(var11 >> 16 & '\uffff'));
         this.putByte(var15 + 4, BufferVertexConsumer.normalIntValue(var12));
         this.putByte(var15 + 5, BufferVertexConsumer.normalIntValue(var13));
         this.putByte(var15 + 6, BufferVertexConsumer.normalIntValue(var14));
         this.nextElementByte += var15 + 8;
         this.endVertex();
      } else {
         super.vertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }
   }

   public Pair<BufferBuilder.DrawState, ByteBuffer> popNextBuffer() {
      BufferBuilder.DrawState var1 = (BufferBuilder.DrawState)this.drawStates.get(this.lastPoppedStateIndex++);
      this.buffer.position(this.totalUploadedBytes);
      this.totalUploadedBytes += Mth.roundToward(var1.bufferSize(), 4);
      this.buffer.limit(this.totalUploadedBytes);
      if (this.lastPoppedStateIndex == this.drawStates.size() && this.vertices == 0) {
         this.clear();
      }

      ByteBuffer var2 = this.buffer.slice();
      this.buffer.clear();
      return Pair.of(var1, var2);
   }

   public void clear() {
      if (this.totalRenderedBytes != this.totalUploadedBytes) {
         LOGGER.warn("Bytes mismatch {} {}", this.totalRenderedBytes, this.totalUploadedBytes);
      }

      this.discard();
   }

   public void discard() {
      this.totalRenderedBytes = 0;
      this.totalUploadedBytes = 0;
      this.nextElementByte = 0;
      this.drawStates.clear();
      this.lastPoppedStateIndex = 0;
   }

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

   public static final class DrawState {
      private final VertexFormat format;
      private final int vertexCount;
      private final int indexCount;
      private final VertexFormat.Mode mode;
      private final VertexFormat.IndexType indexType;
      private final boolean indexOnly;
      private final boolean sequentialIndex;

      private DrawState(VertexFormat var1, int var2, int var3, VertexFormat.Mode var4, VertexFormat.IndexType var5, boolean var6, boolean var7) {
         super();
         this.format = var1;
         this.vertexCount = var2;
         this.indexCount = var3;
         this.mode = var4;
         this.indexType = var5;
         this.indexOnly = var6;
         this.sequentialIndex = var7;
      }

      public VertexFormat format() {
         return this.format;
      }

      public int vertexCount() {
         return this.vertexCount;
      }

      public int indexCount() {
         return this.indexCount;
      }

      public VertexFormat.Mode mode() {
         return this.mode;
      }

      public VertexFormat.IndexType indexType() {
         return this.indexType;
      }

      public int vertexBufferSize() {
         return this.vertexCount * this.format.getVertexSize();
      }

      private int indexBufferSize() {
         return this.sequentialIndex ? 0 : this.indexCount * this.indexType.bytes;
      }

      public int bufferSize() {
         return this.vertexBufferSize() + this.indexBufferSize();
      }

      public boolean indexOnly() {
         return this.indexOnly;
      }

      public boolean sequentialIndex() {
         return this.sequentialIndex;
      }

      // $FF: synthetic method
      DrawState(VertexFormat var1, int var2, int var3, VertexFormat.Mode var4, VertexFormat.IndexType var5, boolean var6, boolean var7, Object var8) {
         this(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public static class SortState {
      private final VertexFormat.Mode mode;
      private final int vertices;
      @Nullable
      private final Vector3f[] sortingPoints;
      private final float sortX;
      private final float sortY;
      private final float sortZ;

      private SortState(VertexFormat.Mode var1, int var2, @Nullable Vector3f[] var3, float var4, float var5, float var6) {
         super();
         this.mode = var1;
         this.vertices = var2;
         this.sortingPoints = var3;
         this.sortX = var4;
         this.sortY = var5;
         this.sortZ = var6;
      }

      // $FF: synthetic method
      SortState(VertexFormat.Mode var1, int var2, Vector3f[] var3, float var4, float var5, float var6, Object var7) {
         this(var1, var2, var3, var4, var5, var6);
      }
   }
}
