package net.minecraft.core;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.Util;

public final class SerializableUUID {
   public static final Codec<UUID> CODEC;

   public static UUID uuidFromIntArray(int[] var0) {
      return new UUID((long)var0[0] << 32 | (long)var0[1] & 4294967295L, (long)var0[2] << 32 | (long)var0[3] & 4294967295L);
   }

   public static int[] uuidToIntArray(UUID var0) {
      long var1 = var0.getMostSignificantBits();
      long var3 = var0.getLeastSignificantBits();
      return leastMostToIntArray(var1, var3);
   }

   private static int[] leastMostToIntArray(long var0, long var2) {
      return new int[]{(int)(var0 >> 32), (int)var0, (int)(var2 >> 32), (int)var2};
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize(var0, 4).map(SerializableUUID::uuidFromIntArray);
      }, (var0) -> {
         return Arrays.stream(uuidToIntArray(var0));
      });
   }
}
