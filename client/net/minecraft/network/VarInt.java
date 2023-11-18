package net.minecraft.network;

import io.netty.buffer.ByteBuf;

public class VarInt {
   private static final int MAX_VARINT_SIZE = 5;
   private static final int DATA_BITS_MASK = 127;
   private static final int CONTINUATION_BIT_MASK = 128;
   private static final int DATA_BITS_PER_BYTE = 7;

   public VarInt() {
      super();
   }

   public static int getByteSize(int var0) {
      for(int var1 = 1; var1 < 5; ++var1) {
         if ((var0 & -1 << var1 * 7) == 0) {
            return var1;
         }
      }

      return 5;
   }

   public static boolean hasContinuationBit(byte var0) {
      return (var0 & 128) == 128;
   }

   public static int read(ByteBuf var0) {
      int var1 = 0;
      int var2 = 0;

      byte var3;
      do {
         var3 = var0.readByte();
         var1 |= (var3 & 127) << var2++ * 7;
         if (var2 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while(hasContinuationBit(var3));

      return var1;
   }

   public static ByteBuf write(ByteBuf var0, int var1) {
      while((var1 & -128) != 0) {
         var0.writeByte(var1 & 127 | 128);
         var1 >>>= 7;
      }

      var0.writeByte(var1);
      return var0;
   }
}
