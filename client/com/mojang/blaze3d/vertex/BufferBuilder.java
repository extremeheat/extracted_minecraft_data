package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder extends DefaultedVertexConsumer implements BufferVertexConsumer {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer buffer;
   private final List<BufferBuilder.DrawState> vertexCounts = Lists.newArrayList();
   private int lastRenderedCountIndex = 0;
   private int totalRenderedBytes = 0;
   private int nextElementByte = 0;
   private int totalUploadedBytes = 0;
   private int vertices;
   @Nullable
   private VertexFormatElement currentElement;
   private int elementIndex;
   private int mode;
   private VertexFormat format;
   private boolean fastFormat;
   private boolean fullFormat;
   private boolean building;

   public BufferBuilder(int var1) {
      super();
      this.buffer = MemoryTracker.createByteBuffer(var1 * 4);
   }

   protected void ensureVertexCapacity() {
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

   public void sortQuads(float var1, float var2, float var3) {
      this.buffer.clear();
      FloatBuffer var4 = this.buffer.asFloatBuffer();
      int var5 = this.vertices / 4;
      float[] var6 = new float[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = getQuadDistanceFromPlayer(var4, var1, var2, var3, this.format.getIntegerSize(), this.totalRenderedBytes / 4 + var7 * this.format.getVertexSize());
      }

      int[] var15 = new int[var5];

      for(int var8 = 0; var8 < var15.length; var15[var8] = var8++) {
      }

      IntArrays.mergeSort(var15, (var1x, var2x) -> {
         return Floats.compare(var6[var2x], var6[var1x]);
      });
      BitSet var16 = new BitSet();
      FloatBuffer var9 = MemoryTracker.createFloatBuffer(this.format.getIntegerSize() * 4);

      for(int var10 = var16.nextClearBit(0); var10 < var15.length; var10 = var16.nextClearBit(var10 + 1)) {
         int var11 = var15[var10];
         if (var11 != var10) {
            this.limitToVertex(var4, var11);
            var9.clear();
            var9.put(var4);
            int var12 = var11;

            for(int var13 = var15[var11]; var12 != var10; var13 = var15[var13]) {
               this.limitToVertex(var4, var13);
               FloatBuffer var14 = var4.slice();
               this.limitToVertex(var4, var12);
               var4.put(var14);
               var16.set(var12);
               var12 = var13;
            }

            this.limitToVertex(var4, var10);
            var9.flip();
            var4.put(var9);
         }

         var16.set(var10);
      }

   }

   private void limitToVertex(FloatBuffer var1, int var2) {
      int var3 = this.format.getIntegerSize() * 4;
      var1.limit(this.totalRenderedBytes / 4 + (var2 + 1) * var3);
      var1.position(this.totalRenderedBytes / 4 + var2 * var3);
   }

   public BufferBuilder.State getState() {
      this.buffer.limit(this.nextElementByte);
      this.buffer.position(this.totalRenderedBytes);
      ByteBuffer var1 = ByteBuffer.allocate(this.vertices * this.format.getVertexSize());
      var1.put(this.buffer);
      this.buffer.clear();
      return new BufferBuilder.State(var1, this.format);
   }

   private static float getQuadDistanceFromPlayer(FloatBuffer var0, float var1, float var2, float var3, int var4, int var5) {
      float var6 = var0.get(var5 + var4 * 0 + 0);
      float var7 = var0.get(var5 + var4 * 0 + 1);
      float var8 = var0.get(var5 + var4 * 0 + 2);
      float var9 = var0.get(var5 + var4 * 1 + 0);
      float var10 = var0.get(var5 + var4 * 1 + 1);
      float var11 = var0.get(var5 + var4 * 1 + 2);
      float var12 = var0.get(var5 + var4 * 2 + 0);
      float var13 = var0.get(var5 + var4 * 2 + 1);
      float var14 = var0.get(var5 + var4 * 2 + 2);
      float var15 = var0.get(var5 + var4 * 3 + 0);
      float var16 = var0.get(var5 + var4 * 3 + 1);
      float var17 = var0.get(var5 + var4 * 3 + 2);
      float var18 = (var6 + var9 + var12 + var15) * 0.25F - var1;
      float var19 = (var7 + var10 + var13 + var16) * 0.25F - var2;
      float var20 = (var8 + var11 + var14 + var17) * 0.25F - var3;
      return var18 * var18 + var19 * var19 + var20 * var20;
   }

   public void restoreState(BufferBuilder.State var1) {
      var1.data.clear();
      int var2 = var1.data.capacity();
      this.ensureCapacity(var2);
      this.buffer.limit(this.buffer.capacity());
      this.buffer.position(this.totalRenderedBytes);
      this.buffer.put(var1.data);
      this.buffer.clear();
      VertexFormat var3 = var1.format;
      this.switchFormat(var3);
      this.vertices = var2 / var3.getVertexSize();
      this.nextElementByte = this.totalRenderedBytes + this.vertices * var3.getVertexSize();
   }

   public void begin(int var1, VertexFormat var2) {
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

   public void end() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         this.building = false;
         this.vertexCounts.add(new BufferBuilder.DrawState(this.format, this.vertices, this.mode));
         this.totalRenderedBytes += this.vertices * this.format.getVertexSize();
         this.vertices = 0;
         this.currentElement = null;
         this.elementIndex = 0;
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
      BufferBuilder.DrawState var1 = (BufferBuilder.DrawState)this.vertexCounts.get(this.lastRenderedCountIndex++);
      this.buffer.position(this.totalUploadedBytes);
      this.totalUploadedBytes += var1.vertexCount() * var1.format().getVertexSize();
      this.buffer.limit(this.totalUploadedBytes);
      if (this.lastRenderedCountIndex == this.vertexCounts.size() && this.vertices == 0) {
         this.clear();
      }

      ByteBuffer var2 = this.buffer.slice();
      this.buffer.clear();
      return Pair.of(var1, var2);
   }

   public void clear() {
      if (this.totalRenderedBytes != this.totalUploadedBytes) {
         LOGGER.warn("Bytes mismatch " + this.totalRenderedBytes + " " + this.totalUploadedBytes);
      }

      this.discard();
   }

   public void discard() {
      this.totalRenderedBytes = 0;
      this.totalUploadedBytes = 0;
      this.nextElementByte = 0;
      this.vertexCounts.clear();
      this.lastRenderedCountIndex = 0;
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
      private final int mode;

      private DrawState(VertexFormat var1, int var2, int var3) {
         super();
         this.format = var1;
         this.vertexCount = var2;
         this.mode = var3;
      }

      public VertexFormat format() {
         return this.format;
      }

      public int vertexCount() {
         return this.vertexCount;
      }

      public int mode() {
         return this.mode;
      }

      // $FF: synthetic method
      DrawState(VertexFormat var1, int var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static class State {
      private final ByteBuffer data;
      private final VertexFormat format;

      private State(ByteBuffer var1, VertexFormat var2) {
         super();
         this.data = var1;
         this.format = var2;
      }

      // $FF: synthetic method
      State(ByteBuffer var1, VertexFormat var2, Object var3) {
         this(var1, var2);
      }
   }
}
