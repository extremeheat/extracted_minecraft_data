package com.mojang.blaze3d.vertex;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;

public class BufferBuilder implements VertexConsumer {
   private static final long NOT_BUILDING = -1L;
   private static final long UNKNOWN_ELEMENT = -1L;
   private static final boolean IS_LITTLE_ENDIAN;
   private final ByteBufferBuilder buffer;
   private long vertexPointer = -1L;
   private int vertices;
   private final VertexFormat format;
   private final VertexFormat.Mode mode;
   private final boolean fastFormat;
   private final boolean fullFormat;
   private final int vertexSize;
   private final int initialElementsToFill;
   private final int[] offsetsByElement;
   private int elementsToFill;
   private boolean building = true;

   public BufferBuilder(ByteBufferBuilder var1, VertexFormat.Mode var2, VertexFormat var3) {
      super();
      if (!var3.contains(VertexFormatElement.POSITION)) {
         throw new IllegalArgumentException("Cannot build mesh with no position element");
      } else {
         this.buffer = var1;
         this.mode = var2;
         this.format = var3;
         this.vertexSize = var3.getVertexSize();
         this.initialElementsToFill = var3.getElementsMask() & ~VertexFormatElement.POSITION.mask();
         this.offsetsByElement = var3.getOffsetsByElement();
         boolean var4 = var3 == DefaultVertexFormat.NEW_ENTITY;
         boolean var5 = var3 == DefaultVertexFormat.BLOCK;
         this.fastFormat = var4 || var5;
         this.fullFormat = var4;
      }
   }

   @Nullable
   public MeshData build() {
      this.ensureBuilding();
      this.endLastVertex();
      MeshData var1 = this.storeMesh();
      this.building = false;
      this.vertexPointer = -1L;
      return var1;
   }

   public MeshData buildOrThrow() {
      MeshData var1 = this.build();
      if (var1 == null) {
         throw new IllegalStateException("BufferBuilder was empty");
      } else {
         return var1;
      }
   }

   private void ensureBuilding() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      }
   }

   @Nullable
   private MeshData storeMesh() {
      if (this.vertices == 0) {
         return null;
      } else {
         ByteBufferBuilder.Result var1 = this.buffer.build();
         if (var1 == null) {
            return null;
         } else {
            int var2 = this.mode.indexCount(this.vertices);
            VertexFormat.IndexType var3 = VertexFormat.IndexType.least(this.vertices);
            return new MeshData(var1, new MeshData.DrawState(this.format, this.vertices, var2, this.mode, var3));
         }
      }
   }

   private long beginVertex() {
      this.ensureBuilding();
      this.endLastVertex();
      ++this.vertices;
      long var1 = this.buffer.reserve(this.vertexSize);
      this.vertexPointer = var1;
      return var1;
   }

   private long beginElement(VertexFormatElement var1) {
      int var2 = this.elementsToFill;
      int var3 = var2 & ~var1.mask();
      if (var3 == var2) {
         return -1L;
      } else {
         this.elementsToFill = var3;
         long var4 = this.vertexPointer;
         if (var4 == -1L) {
            throw new IllegalArgumentException("Not currently building vertex");
         } else {
            return var4 + (long)this.offsetsByElement[var1.id()];
         }
      }
   }

   private void endLastVertex() {
      if (this.vertices != 0) {
         if (this.elementsToFill != 0) {
            Stream var10000 = VertexFormatElement.elementsFromMask(this.elementsToFill);
            VertexFormat var10001 = this.format;
            Objects.requireNonNull(var10001);
            String var3 = (String)var10000.map(var10001::getElementName).collect(Collectors.joining(", "));
            throw new IllegalStateException("Missing elements in vertex: " + var3);
         } else {
            if (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP) {
               long var1 = this.buffer.reserve(this.vertexSize);
               MemoryUtil.memCopy(var1 - (long)this.vertexSize, var1, (long)this.vertexSize);
               ++this.vertices;
            }

         }
      }
   }

   private static void putRgba(long var0, int var2) {
      int var3 = FastColor.ABGR32.fromArgb32(var2);
      MemoryUtil.memPutInt(var0, IS_LITTLE_ENDIAN ? var3 : Integer.reverseBytes(var3));
   }

   private static void putPackedUv(long var0, int var2) {
      if (IS_LITTLE_ENDIAN) {
         MemoryUtil.memPutInt(var0, var2);
      } else {
         MemoryUtil.memPutShort(var0, (short)(var2 & '\uffff'));
         MemoryUtil.memPutShort(var0 + 2L, (short)(var2 >> 16 & '\uffff'));
      }

   }

   public VertexConsumer addVertex(float var1, float var2, float var3) {
      long var4 = this.beginVertex() + (long)this.offsetsByElement[VertexFormatElement.POSITION.id()];
      this.elementsToFill = this.initialElementsToFill;
      MemoryUtil.memPutFloat(var4, var1);
      MemoryUtil.memPutFloat(var4 + 4L, var2);
      MemoryUtil.memPutFloat(var4 + 8L, var3);
      return this;
   }

   public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
      long var5 = this.beginElement(VertexFormatElement.COLOR);
      if (var5 != -1L) {
         MemoryUtil.memPutByte(var5, (byte)var1);
         MemoryUtil.memPutByte(var5 + 1L, (byte)var2);
         MemoryUtil.memPutByte(var5 + 2L, (byte)var3);
         MemoryUtil.memPutByte(var5 + 3L, (byte)var4);
      }

      return this;
   }

   public VertexConsumer setColor(int var1) {
      long var2 = this.beginElement(VertexFormatElement.COLOR);
      if (var2 != -1L) {
         putRgba(var2, var1);
      }

      return this;
   }

   public VertexConsumer setUv(float var1, float var2) {
      long var3 = this.beginElement(VertexFormatElement.UV0);
      if (var3 != -1L) {
         MemoryUtil.memPutFloat(var3, var1);
         MemoryUtil.memPutFloat(var3 + 4L, var2);
      }

      return this;
   }

   public VertexConsumer setUv1(int var1, int var2) {
      return this.uvShort((short)var1, (short)var2, VertexFormatElement.UV1);
   }

   public VertexConsumer setOverlay(int var1) {
      long var2 = this.beginElement(VertexFormatElement.UV1);
      if (var2 != -1L) {
         putPackedUv(var2, var1);
      }

      return this;
   }

   public VertexConsumer setUv2(int var1, int var2) {
      return this.uvShort((short)var1, (short)var2, VertexFormatElement.UV2);
   }

   public VertexConsumer setLight(int var1) {
      long var2 = this.beginElement(VertexFormatElement.UV2);
      if (var2 != -1L) {
         putPackedUv(var2, var1);
      }

      return this;
   }

   private VertexConsumer uvShort(short var1, short var2, VertexFormatElement var3) {
      long var4 = this.beginElement(var3);
      if (var4 != -1L) {
         MemoryUtil.memPutShort(var4, var1);
         MemoryUtil.memPutShort(var4 + 2L, var2);
      }

      return this;
   }

   public VertexConsumer setNormal(float var1, float var2, float var3) {
      long var4 = this.beginElement(VertexFormatElement.NORMAL);
      if (var4 != -1L) {
         MemoryUtil.memPutByte(var4, normalIntValue(var1));
         MemoryUtil.memPutByte(var4 + 1L, normalIntValue(var2));
         MemoryUtil.memPutByte(var4 + 2L, normalIntValue(var3));
      }

      return this;
   }

   private static byte normalIntValue(float var0) {
      return (byte)((int)(Mth.clamp(var0, -1.0F, 1.0F) * 127.0F) & 255);
   }

   public void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
      if (this.fastFormat) {
         long var12 = this.beginVertex();
         MemoryUtil.memPutFloat(var12 + 0L, var1);
         MemoryUtil.memPutFloat(var12 + 4L, var2);
         MemoryUtil.memPutFloat(var12 + 8L, var3);
         putRgba(var12 + 12L, var4);
         MemoryUtil.memPutFloat(var12 + 16L, var5);
         MemoryUtil.memPutFloat(var12 + 20L, var6);
         long var14;
         if (this.fullFormat) {
            putPackedUv(var12 + 24L, var7);
            var14 = var12 + 28L;
         } else {
            var14 = var12 + 24L;
         }

         putPackedUv(var14 + 0L, var8);
         MemoryUtil.memPutByte(var14 + 4L, normalIntValue(var9));
         MemoryUtil.memPutByte(var14 + 5L, normalIntValue(var10));
         MemoryUtil.memPutByte(var14 + 6L, normalIntValue(var11));
      } else {
         VertexConsumer.super.addVertex(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   static {
      IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
   }
}
