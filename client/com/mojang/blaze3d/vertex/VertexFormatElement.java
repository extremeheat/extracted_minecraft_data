package com.mojang.blaze3d.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public record VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
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

   public VertexFormatElement {
      super();
      if (id >= 0 && id < BY_ID.length) {
         if (!this.supportsUsage(index, usage)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
         }
      } else {
         throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
      }
   }

   public static VertexFormatElement register(final int id, final int index, final Type type, final Usage usage, final int count) {
      VertexFormatElement element = new VertexFormatElement(id, index, type, usage, count);
      if (BY_ID[id] != null) {
         throw new IllegalArgumentException("Duplicate element registration for: " + id);
      } else {
         BY_ID[id] = element;
         ELEMENTS.add(element);
         return element;
      }
   }

   private boolean supportsUsage(final int index, final Usage usage) {
      return index == 0 || usage == VertexFormatElement.Usage.UV;
   }

   public String toString() {
      int var10000 = this.count;
      return var10000 + "," + String.valueOf(this.usage) + "," + String.valueOf(this.type) + " (" + this.id + ")";
   }

   public int mask() {
      return 1 << this.id;
   }

   public int byteSize() {
      return this.type.size() * this.count;
   }

   public static @Nullable VertexFormatElement byId(final int id) {
      return BY_ID[id];
   }

   public static Stream<VertexFormatElement> elementsFromMask(final int mask) {
      return ELEMENTS.stream().filter((element) -> (mask & element.mask()) != 0);
   }

   static {
      POSITION = register(0, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      COLOR = register(1, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      UV0 = register(2, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      UV = UV0;
      UV1 = register(3, 1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      UV2 = register(4, 2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      NORMAL = register(5, 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
      LINE_WIDTH = register(6, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1);
   }

   public static enum Usage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      GENERIC("Generic");

      private final String name;

      private Usage(final String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Usage[] $values() {
         return new Usage[]{POSITION, NORMAL, COLOR, UV, GENERIC};
      }
   }

   public static enum Type {
      FLOAT(4, "Float"),
      UBYTE(1, "Unsigned Byte"),
      BYTE(1, "Byte"),
      USHORT(2, "Unsigned Short"),
      SHORT(2, "Short"),
      UINT(4, "Unsigned Int"),
      INT(4, "Int");

      private final int size;
      private final String name;

      private Type(final int size, final String name) {
         this.size = size;
         this.name = name;
      }

      public int size() {
         return this.size;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{FLOAT, UBYTE, BYTE, USHORT, SHORT, UINT, INT};
      }
   }
}
