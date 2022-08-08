package com.mojang.blaze3d.vertex;

import net.minecraft.util.Mth;

public interface BufferVertexConsumer extends VertexConsumer {
   VertexFormatElement currentElement();

   void nextElement();

   void putByte(int var1, byte var2);

   void putShort(int var1, short var2);

   void putFloat(int var1, float var2);

   default VertexConsumer vertex(double var1, double var3, double var5) {
      if (this.currentElement().getUsage() != VertexFormatElement.Usage.POSITION) {
         return this;
      } else if (this.currentElement().getType() == VertexFormatElement.Type.FLOAT && this.currentElement().getCount() == 3) {
         this.putFloat(0, (float)var1);
         this.putFloat(4, (float)var3);
         this.putFloat(8, (float)var5);
         this.nextElement();
         return this;
      } else {
         throw new IllegalStateException();
      }
   }

   default VertexConsumer color(int var1, int var2, int var3, int var4) {
      VertexFormatElement var5 = this.currentElement();
      if (var5.getUsage() != VertexFormatElement.Usage.COLOR) {
         return this;
      } else if (var5.getType() == VertexFormatElement.Type.UBYTE && var5.getCount() == 4) {
         this.putByte(0, (byte)var1);
         this.putByte(1, (byte)var2);
         this.putByte(2, (byte)var3);
         this.putByte(3, (byte)var4);
         this.nextElement();
         return this;
      } else {
         throw new IllegalStateException();
      }
   }

   default VertexConsumer uv(float var1, float var2) {
      VertexFormatElement var3 = this.currentElement();
      if (var3.getUsage() == VertexFormatElement.Usage.UV && var3.getIndex() == 0) {
         if (var3.getType() == VertexFormatElement.Type.FLOAT && var3.getCount() == 2) {
            this.putFloat(0, var1);
            this.putFloat(4, var2);
            this.nextElement();
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         return this;
      }
   }

   default VertexConsumer overlayCoords(int var1, int var2) {
      return this.uvShort((short)var1, (short)var2, 1);
   }

   default VertexConsumer uv2(int var1, int var2) {
      return this.uvShort((short)var1, (short)var2, 2);
   }

   default VertexConsumer uvShort(short var1, short var2, int var3) {
      VertexFormatElement var4 = this.currentElement();
      if (var4.getUsage() == VertexFormatElement.Usage.UV && var4.getIndex() == var3) {
         if (var4.getType() == VertexFormatElement.Type.SHORT && var4.getCount() == 2) {
            this.putShort(0, var1);
            this.putShort(2, var2);
            this.nextElement();
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         return this;
      }
   }

   default VertexConsumer normal(float var1, float var2, float var3) {
      VertexFormatElement var4 = this.currentElement();
      if (var4.getUsage() != VertexFormatElement.Usage.NORMAL) {
         return this;
      } else if (var4.getType() == VertexFormatElement.Type.BYTE && var4.getCount() == 3) {
         this.putByte(0, normalIntValue(var1));
         this.putByte(1, normalIntValue(var2));
         this.putByte(2, normalIntValue(var3));
         this.nextElement();
         return this;
      } else {
         throw new IllegalStateException();
      }
   }

   static byte normalIntValue(float var0) {
      return (byte)((int)(Mth.clamp(var0, -1.0F, 1.0F) * 127.0F) & 255);
   }
}
