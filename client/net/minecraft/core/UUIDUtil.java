package net.minecraft.core;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.Util;

public final class UUIDUtil {
   public static final Codec<UUID> CODEC = Codec.INT_STREAM
      .comapFlatMap(var0 -> Util.fixedSize(var0, 4).map(UUIDUtil::uuidFromIntArray), var0 -> Arrays.stream(uuidToIntArray(var0)));
   public static final int UUID_BYTES = 16;
   private static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";

   private UUIDUtil() {
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

   public static byte[] uuidToByteArray(UUID var0) {
      byte[] var1 = new byte[16];
      ByteBuffer.wrap(var1).order(ByteOrder.BIG_ENDIAN).putLong(var0.getMostSignificantBits()).putLong(var0.getLeastSignificantBits());
      return var1;
   }

   public static UUID readUUID(Dynamic<?> var0) {
      int[] var1 = var0.asIntStream().toArray();
      if (var1.length != 4) {
         throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + var1.length + ".");
      } else {
         return uuidFromIntArray(var1);
      }
   }

   public static UUID getOrCreatePlayerUUID(GameProfile var0) {
      UUID var1 = var0.getId();
      if (var1 == null) {
         var1 = createOfflinePlayerUUID(var0.getName());
      }

      return var1;
   }

   public static UUID createOfflinePlayerUUID(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }
}
