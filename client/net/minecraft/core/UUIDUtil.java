package net.minecraft.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UndashedUuid;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class UUIDUtil {
   public static final Codec<UUID> CODEC;
   public static final Codec<Set<UUID>> CODEC_SET;
   public static final Codec<Set<UUID>> CODEC_LINKED_SET;
   public static final Codec<UUID> STRING_CODEC;
   public static Codec<UUID> AUTHLIB_CODEC;
   public static Codec<UUID> LENIENT_CODEC;
   public static StreamCodec<ByteBuf, UUID> STREAM_CODEC;
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

   public static UUID createOfflinePlayerUUID(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }

   public static GameProfile createOfflineProfile(String var0) {
      UUID var1 = createOfflinePlayerUUID(var0);
      return new GameProfile(var1, var0);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize((IntStream)var0, 4).map(UUIDUtil::uuidFromIntArray);
      }, (var0) -> {
         return Arrays.stream(uuidToIntArray(var0));
      });
      CODEC_SET = Codec.list(CODEC).xmap(Sets::newHashSet, Lists::newArrayList);
      CODEC_LINKED_SET = Codec.list(CODEC).xmap(Sets::newLinkedHashSet, Lists::newArrayList);
      STRING_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(UUID.fromString(var0), Lifecycle.stable());
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Invalid UUID " + var0 + ": " + var2.getMessage();
            });
         }
      }, UUID::toString);
      AUTHLIB_CODEC = Codec.withAlternative(Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(UndashedUuid.fromStringLenient(var0), Lifecycle.stable());
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Invalid UUID " + var0 + ": " + var2.getMessage();
            });
         }
      }, UndashedUuid::toString), CODEC);
      LENIENT_CODEC = Codec.withAlternative(CODEC, STRING_CODEC);
      STREAM_CODEC = new StreamCodec<ByteBuf, UUID>() {
         public UUID decode(ByteBuf var1) {
            return FriendlyByteBuf.readUUID(var1);
         }

         public void encode(ByteBuf var1, UUID var2) {
            FriendlyByteBuf.writeUUID(var1, var2);
         }

         // $FF: synthetic method
         public void encode(Object var1, Object var2) {
            this.encode((ByteBuf)var1, (UUID)var2);
         }

         // $FF: synthetic method
         public Object decode(Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
   }
}
