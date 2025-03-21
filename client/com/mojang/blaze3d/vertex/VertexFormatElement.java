package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.DontObfuscate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@DontObfuscate
public record VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
   public static final int MAX_COUNT = 32;
   private static final VertexFormatElement[] BY_ID = new VertexFormatElement[32];
   private static final List<VertexFormatElement> ELEMENTS = new ArrayList(32);
   public static final VertexFormatElement POSITION;
   public static final VertexFormatElement COLOR;
   public static final VertexFormatElement UV0;
   public static final VertexFormatElement UV;
   public static final VertexFormatElement UV1;
   public static final VertexFormatElement UV2;
   public static final VertexFormatElement NORMAL;

   public VertexFormatElement(int var1, int var2, Type var3, Usage var4, int var5) {
      super();
      if (var1 >= 0 && var1 < BY_ID.length) {
         if (!this.supportsUsage(var2, var4)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
         } else {
            this.id = var1;
            this.index = var2;
            this.type = var3;
            this.usage = var4;
            this.count = var5;
         }
      } else {
         throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
      }
   }

   public static VertexFormatElement register(int var0, int var1, Type var2, Usage var3, int var4) {
      VertexFormatElement var5 = new VertexFormatElement(var0, var1, var2, var3, var4);
      if (BY_ID[var0] != null) {
         throw new IllegalArgumentException("Duplicate element registration for: " + var0);
      } else {
         BY_ID[var0] = var5;
         ELEMENTS.add(var5);
         return var5;
      }
   }

   private boolean supportsUsage(int var1, Usage var2) {
      return var1 == 0 || var2 == VertexFormatElement.Usage.UV;
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

   @Nullable
   public static VertexFormatElement byId(int var0) {
      return BY_ID[var0];
   }

   public static Stream<VertexFormatElement> elementsFromMask(int var0) {
      return ELEMENTS.stream().filter((var1) -> var1 != null && (var0 & var1.mask()) != 0);
   }

   static {
      POSITION = register(0, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      COLOR = register(1, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      UV0 = register(2, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      UV = UV0;
      UV1 = register(3, 1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      UV2 = register(4, 2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      NORMAL = register(5, 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
   }

   @DontObfuscate
   public static enum Usage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      GENERIC("Generic");

      private final String name;

      private Usage(final String var3) {
         this.name = var3;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Usage[] $values() {
         return new Usage[]{POSITION, NORMAL, COLOR, UV, GENERIC};
      }
   }

   @DontObfuscate
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

      private Type(final int var3, final String var4) {
         this.size = var3;
         this.name = var4;
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
