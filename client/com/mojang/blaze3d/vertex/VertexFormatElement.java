package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public record VertexFormatElement(int id, int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
   public static final int MAX_COUNT = 32;
   private static final VertexFormatElement[] BY_ID = new VertexFormatElement[32];
   private static final List<VertexFormatElement> ELEMENTS = new ArrayList<>(32);
   public static final VertexFormatElement POSITION = register(0, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
   public static final VertexFormatElement COLOR = register(1, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
   public static final VertexFormatElement UV0 = register(2, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement UV = UV0;
   public static final VertexFormatElement UV1 = register(3, 1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement UV2 = register(4, 2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement NORMAL = register(5, 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);

   public VertexFormatElement(int id, int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
      super();
      if (id < 0 || id >= BY_ID.length) {
         throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
      } else if (!this.supportsUsage(index, usage)) {
         throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
      } else {
         this.id = id;
         this.index = index;
         this.type = type;
         this.usage = usage;
         this.count = count;
      }
   }

   public static VertexFormatElement register(int var0, int var1, VertexFormatElement.Type var2, VertexFormatElement.Usage var3, int var4) {
      VertexFormatElement var5 = new VertexFormatElement(var0, var1, var2, var3, var4);
      if (BY_ID[var0] != null) {
         throw new IllegalArgumentException("Duplicate element registration for: " + var0);
      } else {
         BY_ID[var0] = var5;
         ELEMENTS.add(var5);
         return var5;
      }
   }

   private boolean supportsUsage(int var1, VertexFormatElement.Usage var2) {
      return var1 == 0 || var2 == VertexFormatElement.Usage.UV;
   }

   public String toString() {
      return this.count + "," + this.usage + "," + this.type + " (" + this.id + ")";
   }

   public int mask() {
      return 1 << this.id;
   }

   public int byteSize() {
      return this.type.size() * this.count;
   }

   public void setupBufferState(int var1, long var2, int var4) {
      this.usage.setupState.setupBufferState(this.count, this.type.glType(), var4, var2, var1);
   }

   @Nullable
   public static VertexFormatElement byId(int var0) {
      return BY_ID[var0];
   }

   public static Stream<VertexFormatElement> elementsFromMask(int var0) {
      return ELEMENTS.stream().filter(var1 -> var1 != null && (var0 & var1.mask()) != 0);
   }

   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glType;

      private Type(final int nullxx, final String nullxxx, final int nullxxxx) {
         this.size = nullxx;
         this.name = nullxxx;
         this.glType = nullxxxx;
      }

      public int size() {
         return this.size;
      }

      public int glType() {
         return this.glType;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public static enum Usage {
      POSITION("Position", (var0, var1, var2, var3, var5) -> GlStateManager._vertexAttribPointer(var5, var0, var1, false, var2, var3)),
      NORMAL("Normal", (var0, var1, var2, var3, var5) -> GlStateManager._vertexAttribPointer(var5, var0, var1, true, var2, var3)),
      COLOR("Vertex Color", (var0, var1, var2, var3, var5) -> GlStateManager._vertexAttribPointer(var5, var0, var1, true, var2, var3)),
      UV("UV", (var0, var1, var2, var3, var5) -> {
         if (var1 == 5126) {
            GlStateManager._vertexAttribPointer(var5, var0, var1, false, var2, var3);
         } else {
            GlStateManager._vertexAttribIPointer(var5, var0, var1, var2, var3);
         }
      }),
      GENERIC("Generic", (var0, var1, var2, var3, var5) -> GlStateManager._vertexAttribPointer(var5, var0, var1, false, var2, var3));

      private final String name;
      final VertexFormatElement.Usage.SetupState setupState;

      private Usage(final String nullxx, final VertexFormatElement.Usage.SetupState nullxxx) {
         this.name = nullxx;
         this.setupState = nullxxx;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @FunctionalInterface
      interface SetupState {
         void setupBufferState(int var1, int var2, int var3, long var4, int var6);
      }
   }
}