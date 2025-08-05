package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LpVec3 {
   private static final int DATA_BITS_MASK = 32767;
   private static final int MAPPED_MAX_VALUE = 16383;
   private static final int SCALE_BITS_MASK = 3;
   private static final int CONTINUATION_BIT_MASK = 4;

   public LpVec3() {
      super();
   }

   public static boolean hasContinuationBit(int var0) {
      return (var0 & 4) == 4;
   }

   public static Vec3 read(ByteBuf var0) {
      short var1 = var0.readUnsignedByte();
      if (var1 == 0) {
         return Vec3.ZERO;
      } else {
         short var2 = var0.readUnsignedByte();
         long var3 = var0.readUnsignedInt();
         long var5 = var3 << 16 | (long)(var2 << 8) | (long)var1;
         double var7 = (double)(var5 >> 3 & 32767L);
         double var9 = (double)(var5 >> 18 & 32767L);
         double var11 = (double)(var5 >> 33 & 32767L);
         int var13 = var1 & 3;
         if (hasContinuationBit(var1)) {
            var13 |= VarInt.read(var0) << 2;
         }

         return new Vec3((var7 / 16383.0 - 1.0) * (double)var13, (var9 / 16383.0 - 1.0) * (double)var13, (var11 / 16383.0 - 1.0) * (double)var13);
      }
   }

   public static void write(ByteBuf var0, Vec3 var1) {
      double var2 = Math.max(Math.abs(var1.x), Math.max(Math.abs(var1.y), Math.abs(var1.z)));
      if (var2 < 9.999999747378752E-6) {
         var0.writeByte(0);
      } else {
         int var4 = Mth.ceil(var2);
         boolean var5 = (var4 & 3) != var4;
         double var6 = 0.5 / (double)var4;
         long var8 = (long)((var1.x * var6 + 0.5) * 32767.0);
         long var10 = (long)((var1.y * var6 + 0.5) * 32767.0);
         long var12 = (long)((var1.z * var6 + 0.5) * 32767.0);
         int var14 = var5 ? var4 & 3 | 4 : var4;
         long var15 = (long)var14 | var8 << 3 | var10 << 18 | var12 << 33;
         var0.writeByte((byte)((int)var15));
         var0.writeByte((byte)((int)(var15 >> 8)));
         var0.writeInt((int)(var15 >> 16));
         if (var5) {
            VarInt.write(var0, var4 >> 2);
         }

      }
   }
}
