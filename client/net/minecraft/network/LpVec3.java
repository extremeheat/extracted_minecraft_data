package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LpVec3 {
   private static final int DATA_BITS = 15;
   private static final int DATA_BITS_MASK = 32767;
   private static final double MAX_QUANTIZED_VALUE = 32766.0;
   private static final int SCALE_BITS = 2;
   private static final int SCALE_BITS_MASK = 3;
   private static final int CONTINUATION_FLAG = 4;
   private static final int X_OFFSET = 3;
   private static final int Y_OFFSET = 18;
   private static final int Z_OFFSET = 33;
   public static final double ABS_MAX_VALUE = 1.7179869183E10;
   public static final double ABS_MIN_VALUE = 3.051944088384301E-5;

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
         long var7 = (long)(var1 & 3);
         if (hasContinuationBit(var1)) {
            var7 |= ((long)VarInt.read(var0) & 4294967295L) << 2;
         }

         return new Vec3(unpack(var5 >> 3) * (double)var7, unpack(var5 >> 18) * (double)var7, unpack(var5 >> 33) * (double)var7);
      }
   }

   public static void write(ByteBuf var0, Vec3 var1) {
      double var2 = sanitize(var1.x);
      double var4 = sanitize(var1.y);
      double var6 = sanitize(var1.z);
      double var8 = Mth.absMax(var2, Mth.absMax(var4, var6));
      if (var8 < 3.051944088384301E-5) {
         var0.writeByte(0);
      } else {
         long var10 = Mth.ceilLong(var8);
         boolean var12 = (var10 & 3L) != var10;
         long var13 = var12 ? var10 & 3L | 4L : var10;
         long var15 = pack(var2 / (double)var10) << 3;
         long var17 = pack(var4 / (double)var10) << 18;
         long var19 = pack(var6 / (double)var10) << 33;
         long var21 = var13 | var15 | var17 | var19;
         var0.writeByte((byte)((int)var21));
         var0.writeByte((byte)((int)(var21 >> 8)));
         var0.writeInt((int)(var21 >> 16));
         if (var12) {
            VarInt.write(var0, (int)(var10 >> 2));
         }

      }
   }

   private static double sanitize(double var0) {
      return Double.isNaN(var0) ? 0.0 : Math.clamp(var0, -1.7179869183E10, 1.7179869183E10);
   }

   private static long pack(double var0) {
      return Math.round((var0 * 0.5 + 0.5) * 32766.0);
   }

   private static double unpack(long var0) {
      return Math.min((double)(var0 & 32767L), 32766.0) * 2.0 / 32766.0 - 1.0;
   }
}
