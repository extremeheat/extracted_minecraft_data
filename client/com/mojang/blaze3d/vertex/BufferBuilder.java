package com.mojang.blaze3d.vertex;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class BufferBuilder implements VertexConsumer {
   private static final int MAX_VERTEX_COUNT = 16777215;
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

   public BufferBuilder(final ByteBufferBuilder buffer, final VertexFormat.Mode mode, final VertexFormat format) {
      super();
      if (!format.contains(VertexFormatElement.POSITION)) {
         throw new IllegalArgumentException("Cannot build mesh with no position element");
      } else {
         this.buffer = buffer;
         this.mode = mode;
         this.format = format;
         this.vertexSize = format.getVertexSize();
         this.initialElementsToFill = format.getElementsMask() & ~VertexFormatElement.POSITION.mask();
         this.offsetsByElement = format.getOffsetsByElement();
         boolean isFullFormat = format == DefaultVertexFormat.ENTITY;
         boolean isBlockFormat = format == DefaultVertexFormat.BLOCK;
         this.fastFormat = isFullFormat || isBlockFormat;
         this.fullFormat = isFullFormat;
      }
   }

   public @Nullable MeshData build() {
      this.ensureBuilding();
      this.endLastVertex();
      MeshData mesh = this.storeMesh();
      this.building = false;
      this.vertexPointer = -1L;
      return mesh;
   }

   public MeshData buildOrThrow() {
      MeshData buffer = this.build();
      if (buffer == null) {
         throw new IllegalStateException("BufferBuilder was empty");
      } else {
         return buffer;
      }
   }

   private void ensureBuilding() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      }
   }

   private @Nullable MeshData storeMesh() {
      if (this.vertices == 0) {
         return null;
      } else {
         ByteBufferBuilder.Result vertexBuffer = this.buffer.build();
         if (vertexBuffer == null) {
            return null;
         } else {
            int indices = this.mode.indexCount(this.vertices);
            VertexFormat.IndexType indexType = VertexFormat.IndexType.least(this.vertices);
            return new MeshData(vertexBuffer, new MeshData.DrawState(this.format, this.vertices, indices, this.mode, indexType));
         }
      }
   }

   private long beginVertex() {
      this.ensureBuilding();
      this.endLastVertex();
      if (this.vertices >= 16777215) {
         throw new IllegalStateException("Trying to write too many vertices (>16777215) into BufferBuilder");
      } else {
         ++this.vertices;
         long pointer = this.buffer.reserve(this.vertexSize);
         this.vertexPointer = pointer;
         return pointer;
      }
   }

   private long beginElement(final VertexFormatElement element) {
      int oldElements = this.elementsToFill;
      int newElements = oldElements & ~element.mask();
      if (newElements == oldElements) {
         return -1L;
      } else {
         this.elementsToFill = newElements;
         long vertexPointer = this.vertexPointer;
         if (vertexPointer == -1L) {
            throw new IllegalArgumentException("Not currently building vertex");
         } else {
            return vertexPointer + (long)this.offsetsByElement[element.id()];
         }
      }
   }

   private void endLastVertex() {
      if (this.vertices != 0) {
         if (this.elementsToFill != 0) {
            Stream var10000 = VertexFormatElement.elementsFromMask(this.elementsToFill);
            VertexFormat var10001 = this.format;
            Objects.requireNonNull(var10001);
            String missingElements = (String)var10000.map(var10001::getElementName).collect(Collectors.joining(", "));
            throw new IllegalStateException("Missing elements in vertex: " + missingElements);
         } else {
            if (this.mode == VertexFormat.Mode.LINES) {
               long pointer = this.buffer.reserve(this.vertexSize);
               MemoryUtil.memCopy(pointer - (long)this.vertexSize, pointer, (long)this.vertexSize);
               ++this.vertices;
            }

         }
      }
   }

   private static void putRgba(final long pointer, final int argb) {
      int abgr = ARGB.toABGR(argb);
      MemoryUtil.memPutInt(pointer, IS_LITTLE_ENDIAN ? abgr : Integer.reverseBytes(abgr));
   }

   private static void putPackedUv(final long pointer, final int packedUv) {
      if (IS_LITTLE_ENDIAN) {
         MemoryUtil.memPutInt(pointer, packedUv);
      } else {
         MemoryUtil.memPutShort(pointer, (short)(packedUv & '\uffff'));
         MemoryUtil.memPutShort(pointer + 2L, (short)(packedUv >> 16 & '\uffff'));
      }

   }

   public VertexConsumer addVertex(final float x, final float y, final float z) {
      long pointer = this.beginVertex() + (long)this.offsetsByElement[VertexFormatElement.POSITION.id()];
      this.elementsToFill = this.initialElementsToFill;
      MemoryUtil.memPutFloat(pointer, x);
      MemoryUtil.memPutFloat(pointer + 4L, y);
      MemoryUtil.memPutFloat(pointer + 8L, z);
      return this;
   }

   public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
      long pointer = this.beginElement(VertexFormatElement.COLOR);
      if (pointer != -1L) {
         MemoryUtil.memPutByte(pointer, (byte)r);
         MemoryUtil.memPutByte(pointer + 1L, (byte)g);
         MemoryUtil.memPutByte(pointer + 2L, (byte)b);
         MemoryUtil.memPutByte(pointer + 3L, (byte)a);
      }

      return this;
   }

   public VertexConsumer setColor(final int color) {
      long pointer = this.beginElement(VertexFormatElement.COLOR);
      if (pointer != -1L) {
         putRgba(pointer, color);
      }

      return this;
   }

   public VertexConsumer setUv(final float u, final float v) {
      long pointer = this.beginElement(VertexFormatElement.UV0);
      if (pointer != -1L) {
         MemoryUtil.memPutFloat(pointer, u);
         MemoryUtil.memPutFloat(pointer + 4L, v);
      }

      return this;
   }

   public VertexConsumer setUv1(final int u, final int v) {
      return this.uvShort((short)u, (short)v, VertexFormatElement.UV1);
   }

   public VertexConsumer setOverlay(final int packedOverlayCoords) {
      long pointer = this.beginElement(VertexFormatElement.UV1);
      if (pointer != -1L) {
         putPackedUv(pointer, packedOverlayCoords);
      }

      return this;
   }

   public VertexConsumer setUv2(final int u, final int v) {
      return this.uvShort((short)u, (short)v, VertexFormatElement.UV2);
   }

   public VertexConsumer setLight(final int packedLightCoords) {
      long pointer = this.beginElement(VertexFormatElement.UV2);
      if (pointer != -1L) {
         putPackedUv(pointer, packedLightCoords);
      }

      return this;
   }

   private VertexConsumer uvShort(final short u, final short v, final VertexFormatElement element) {
      long pointer = this.beginElement(element);
      if (pointer != -1L) {
         MemoryUtil.memPutShort(pointer, u);
         MemoryUtil.memPutShort(pointer + 2L, v);
      }

      return this;
   }

   public VertexConsumer setNormal(final float x, final float y, final float z) {
      long pointer = this.beginElement(VertexFormatElement.NORMAL);
      if (pointer != -1L) {
         MemoryUtil.memPutByte(pointer, normalIntValue(x));
         MemoryUtil.memPutByte(pointer + 1L, normalIntValue(y));
         MemoryUtil.memPutByte(pointer + 2L, normalIntValue(z));
      }

      return this;
   }

   public VertexConsumer setLineWidth(final float width) {
      long pointer = this.beginElement(VertexFormatElement.LINE_WIDTH);
      if (pointer != -1L) {
         MemoryUtil.memPutFloat(pointer, width);
      }

      return this;
   }

   private static byte normalIntValue(final float c) {
      return (byte)((int)(Mth.clamp(c, -1.0F, 1.0F) * 127.0F) & 255);
   }

   public void addVertex(final float x, final float y, final float z, final int color, final float u, final float v, final int overlayCoords, final int lightCoords, final float nx, final float ny, final float nz) {
      if (this.fastFormat) {
         long pointer = this.beginVertex();
         MemoryUtil.memPutFloat(pointer + 0L, x);
         MemoryUtil.memPutFloat(pointer + 4L, y);
         MemoryUtil.memPutFloat(pointer + 8L, z);
         putRgba(pointer + 12L, color);
         MemoryUtil.memPutFloat(pointer + 16L, u);
         MemoryUtil.memPutFloat(pointer + 20L, v);
         long lightStart;
         if (this.fullFormat) {
            putPackedUv(pointer + 24L, overlayCoords);
            lightStart = pointer + 28L;
         } else {
            lightStart = pointer + 24L;
         }

         putPackedUv(lightStart + 0L, lightCoords);
         if (this.fullFormat) {
            MemoryUtil.memPutByte(lightStart + 4L, normalIntValue(nx));
            MemoryUtil.memPutByte(lightStart + 5L, normalIntValue(ny));
            MemoryUtil.memPutByte(lightStart + 6L, normalIntValue(nz));
         }

      } else {
         VertexConsumer.super.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz);
      }
   }

   static {
      IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
   }
}
