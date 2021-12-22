package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;

public class VertexFormatElement {
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int count;
   private final int byteSize;

   public VertexFormatElement(int var1, VertexFormatElement.Type var2, VertexFormatElement.Usage var3, int var4) {
      super();
      if (this.supportsUsage(var1, var3)) {
         this.usage = var3;
         this.type = var2;
         this.index = var1;
         this.count = var4;
         this.byteSize = var2.getSize() * this.count;
      } else {
         throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
      }
   }

   private boolean supportsUsage(int var1, VertexFormatElement.Usage var2) {
      return var1 == 0 || var2 == VertexFormatElement.Usage.field_183;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getCount() {
      return this.count;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      int var10000 = this.count;
      return var10000 + "," + this.usage.getName() + "," + this.type.getName();
   }

   public final int getByteSize() {
      return this.byteSize;
   }

   public final boolean isPosition() {
      return this.usage == VertexFormatElement.Usage.POSITION;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormatElement var2 = (VertexFormatElement)var1;
         if (this.count != var2.count) {
            return false;
         } else if (this.index != var2.index) {
            return false;
         } else if (this.type != var2.type) {
            return false;
         } else {
            return this.usage == var2.usage;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = 31 * var1 + this.usage.hashCode();
      var1 = 31 * var1 + this.index;
      var1 = 31 * var1 + this.count;
      return var1;
   }

   public void setupBufferState(int var1, long var2, int var4) {
      this.usage.setupBufferState(this.count, this.type.getGlType(), var4, var2, this.index, var1);
   }

   public void clearBufferState(int var1) {
      this.usage.clearBufferState(this.index, var1);
   }

   public static enum Usage {
      POSITION("Position", (var0, var1, var2, var3, var5, var6) -> {
         GlStateManager._enableVertexAttribArray(var6);
         GlStateManager._vertexAttribPointer(var6, var0, var1, false, var2, var3);
      }, (var0, var1) -> {
         GlStateManager._disableVertexAttribArray(var1);
      }),
      NORMAL("Normal", (var0, var1, var2, var3, var5, var6) -> {
         GlStateManager._enableVertexAttribArray(var6);
         GlStateManager._vertexAttribPointer(var6, var0, var1, true, var2, var3);
      }, (var0, var1) -> {
         GlStateManager._disableVertexAttribArray(var1);
      }),
      COLOR("Vertex Color", (var0, var1, var2, var3, var5, var6) -> {
         GlStateManager._enableVertexAttribArray(var6);
         GlStateManager._vertexAttribPointer(var6, var0, var1, true, var2, var3);
      }, (var0, var1) -> {
         GlStateManager._disableVertexAttribArray(var1);
      }),
      // $FF: renamed from: UV com.mojang.blaze3d.vertex.VertexFormatElement$Usage
      field_183("UV", (var0, var1, var2, var3, var5, var6) -> {
         GlStateManager._enableVertexAttribArray(var6);
         if (var1 == 5126) {
            GlStateManager._vertexAttribPointer(var6, var0, var1, false, var2, var3);
         } else {
            GlStateManager._vertexAttribIPointer(var6, var0, var1, var2, var3);
         }

      }, (var0, var1) -> {
         GlStateManager._disableVertexAttribArray(var1);
      }),
      PADDING("Padding", (var0, var1, var2, var3, var5, var6) -> {
      }, (var0, var1) -> {
      }),
      GENERIC("Generic", (var0, var1, var2, var3, var5, var6) -> {
         GlStateManager._enableVertexAttribArray(var6);
         GlStateManager._vertexAttribPointer(var6, var0, var1, false, var2, var3);
      }, (var0, var1) -> {
         GlStateManager._disableVertexAttribArray(var1);
      });

      private final String name;
      private final VertexFormatElement.Usage.SetupState setupState;
      private final VertexFormatElement.Usage.ClearState clearState;

      private Usage(String var3, VertexFormatElement.Usage.SetupState var4, VertexFormatElement.Usage.ClearState var5) {
         this.name = var3;
         this.setupState = var4;
         this.clearState = var5;
      }

      void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7) {
         this.setupState.setupBufferState(var1, var2, var3, var4, var6, var7);
      }

      public void clearBufferState(int var1, int var2) {
         this.clearState.clearBufferState(var1, var2);
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static VertexFormatElement.Usage[] $values() {
         return new VertexFormatElement.Usage[]{POSITION, NORMAL, COLOR, field_183, PADDING, GENERIC};
      }

      @FunctionalInterface
      private interface SetupState {
         void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
      }

      @FunctionalInterface
      private interface ClearState {
         void clearBufferState(int var1, int var2);
      }
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

      private Type(int var3, String var4, int var5) {
         this.size = var3;
         this.name = var4;
         this.glType = var5;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlType() {
         return this.glType;
      }

      // $FF: synthetic method
      private static VertexFormatElement.Type[] $values() {
         return new VertexFormatElement.Type[]{FLOAT, UBYTE, BYTE, USHORT, SHORT, UINT, INT};
      }
   }
}
