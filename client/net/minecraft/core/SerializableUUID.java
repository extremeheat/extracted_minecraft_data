package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.Util;

public final class SerializableUUID {
   public static final Codec<UUID> CODEC;

   private SerializableUUID() {
      super();
   }

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

   public static UUID readUUID(Dynamic<?> var0) {
      int[] var1 = var0.asIntStream().toArray();
      if (var1.length != 4) {
         throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + var1.length + ".");
      } else {
         return uuidFromIntArray(var1);
      }
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize((IntStream)var0, 4).map(SerializableUUID::uuidFromIntArray);
      }, (var0) -> {
         return Arrays.stream(uuidToIntArray(var0));
      });
   }
}
