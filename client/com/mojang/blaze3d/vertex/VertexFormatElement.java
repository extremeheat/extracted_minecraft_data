package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.GpuFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public record VertexFormatElement(int id, int index, GpuFormat format) {
   public static final int MAX_COUNT = 32;
   private static final @Nullable VertexFormatElement[] BY_ID = new VertexFormatElement[32];
   private static final List<VertexFormatElement> ELEMENTS = new ArrayList(32);
   public static final VertexFormatElement POSITION;
   public static final VertexFormatElement COLOR;
   public static final VertexFormatElement UV0;
   public static final VertexFormatElement UV;
   public static final VertexFormatElement UV1;
   public static final VertexFormatElement UV2;
   public static final VertexFormatElement NORMAL;
   public static final VertexFormatElement LINE_WIDTH;

   public VertexFormatElement(int id, int index, GpuFormat format) {
      super();
      if (id >= 0 && id < BY_ID.length) {
         this.id = id;
         this.index = index;
         this.format = format;
      } else {
         throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
      }
   }

   public static VertexFormatElement register(final int id, final int index, final GpuFormat format) {
      VertexFormatElement element = new VertexFormatElement(id, index, format);
      if (BY_ID[id] != null) {
         throw new IllegalArgumentException("Duplicate element registration for: " + id);
      } else {
         BY_ID[id] = element;
         ELEMENTS.add(element);
         return element;
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.format);
      return var10000 + " (" + this.id + ")";
   }

   public int mask() {
      return 1 << this.id;
   }

   public int byteSize() {
      return this.format.pixelSize();
   }

   public static @Nullable VertexFormatElement byId(final int id) {
      return BY_ID[id];
   }

   public static Stream<VertexFormatElement> elementsFromMask(final int mask) {
      return ELEMENTS.stream().filter((element) -> (mask & element.mask()) != 0);
   }

   static {
      POSITION = register(0, 0, GpuFormat.RGB32_FLOAT);
      COLOR = register(1, 0, GpuFormat.RGBA8_UNORM);
      UV0 = register(2, 0, GpuFormat.RG32_FLOAT);
      UV = UV0;
      UV1 = register(3, 1, GpuFormat.RG16_SINT);
      UV2 = register(4, 2, GpuFormat.RG16_SINT);
      NORMAL = register(5, 0, GpuFormat.RGB8_SNORM);
      LINE_WIDTH = register(6, 0, GpuFormat.R32_FLOAT);
   }
}
