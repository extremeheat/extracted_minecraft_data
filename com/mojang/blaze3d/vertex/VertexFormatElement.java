package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int count;
   private final int byteSize;

   public VertexFormatElement(int var1, VertexFormatElement.Type var2, VertexFormatElement.Usage var3, int var4) {
      if (this.supportsUsage(var1, var3)) {
         this.usage = var3;
      } else {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.Usage.UV;
      }

      this.type = var2;
      this.index = var1;
      this.count = var4;
      this.byteSize = var2.getSize() * this.count;
   }

   private boolean supportsUsage(int var1, VertexFormatElement.Usage var2) {
      return var1 == 0 || var2 == VertexFormatElement.Usage.UV;
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
      return this.count + "," + this.usage.getName() + "," + this.type.getName();
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

   public void setupBufferState(long var1, int var3) {
      this.usage.setupBufferState(this.count, this.type.getGlType(), var3, var1, this.index);
   }

   public void clearBufferState() {
      this.usage.clearBufferState(this.index);
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
   }

   public static enum Usage {
      POSITION("Position", (var0, var1, var2, var3, var5) -> {
         GlStateManager._vertexPointer(var0, var1, var2, var3);
         GlStateManager._enableClientState(32884);
      }, (var0) -> {
         GlStateManager._disableClientState(32884);
      }),
      NORMAL("Normal", (var0, var1, var2, var3, var5) -> {
         GlStateManager._normalPointer(var1, var2, var3);
         GlStateManager._enableClientState(32885);
      }, (var0) -> {
         GlStateManager._disableClientState(32885);
      }),
      COLOR("Vertex Color", (var0, var1, var2, var3, var5) -> {
         GlStateManager._colorPointer(var0, var1, var2, var3);
         GlStateManager._enableClientState(32886);
      }, (var0) -> {
         GlStateManager._disableClientState(32886);
         GlStateManager._clearCurrentColor();
      }),
      UV("UV", (var0, var1, var2, var3, var5) -> {
         GlStateManager._glClientActiveTexture('蓀' + var5);
         GlStateManager._texCoordPointer(var0, var1, var2, var3);
         GlStateManager._enableClientState(32888);
         GlStateManager._glClientActiveTexture(33984);
      }, (var0) -> {
         GlStateManager._glClientActiveTexture('蓀' + var0);
         GlStateManager._disableClientState(32888);
         GlStateManager._glClientActiveTexture(33984);
      }),
      PADDING("Padding", (var0, var1, var2, var3, var5) -> {
      }, (var0) -> {
      }),
      GENERIC("Generic", (var0, var1, var2, var3, var5) -> {
         GlStateManager._enableVertexAttribArray(var5);
         GlStateManager._vertexAttribPointer(var5, var0, var1, false, var2, var3);
      }, GlStateManager::_disableVertexAttribArray);

      private final String name;
      private final VertexFormatElement.Usage.SetupState setupState;
      private final IntConsumer clearState;

      private Usage(String var3, VertexFormatElement.Usage.SetupState var4, IntConsumer var5) {
         this.name = var3;
         this.setupState = var4;
         this.clearState = var5;
      }

      private void setupBufferState(int var1, int var2, int var3, long var4, int var6) {
         this.setupState.setupBufferState(var1, var2, var3, var4, var6);
      }

      public void clearBufferState(int var1) {
         this.clearState.accept(var1);
      }

      public String getName() {
         return this.name;
      }

      interface SetupState {
         void setupBufferState(int var1, int var2, int var3, long var4, int var6);
      }
   }
}
