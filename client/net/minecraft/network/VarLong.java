package net.minecraft.network;

import io.netty.buffer.ByteBuf;

public class VarLong {
   private static final int MAX_VARLONG_SIZE = 10;
   private static final int DATA_BITS_MASK = 127;
   private static final int CONTINUATION_BIT_MASK = 128;
   private static final int DATA_BITS_PER_BYTE = 7;

   public VarLong() {
      super();
   }

   public static int getByteSize(long var0) {
      for(int var2 = 1; var2 < 10; ++var2) {
         if ((var0 & -1L << var2 * 7) == 0L) {
            return var2;
         }
      }

      return 10;
   }

   public static boolean hasContinuationBit(byte var0) {
      return (var0 & 128) == 128;
   }

   public static long read(ByteBuf var0) {
      long var1 = 0L;
      int var3 = 0;

      byte var4;
      do {
         var4 = var0.readByte();
         var1 |= (long)(var4 & 127) << var3++ * 7;
         if (var3 > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while(hasContinuationBit(var4));

      return var1;
   }

   public static ByteBuf write(ByteBuf var0, long var1) {
      while((var1 & -128L) != 0L) {
         var0.writeByte((int)(var1 & 127L) | 128);
         var1 >>>= 7;
      }

      var0.writeByte((int)var1);
      return var0;
   }
}
