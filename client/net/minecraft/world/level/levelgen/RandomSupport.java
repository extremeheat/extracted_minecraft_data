package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
   public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   public static final long SILVER_RATIO_64 = 7640891576956012809L;
   private static final HashFunction MD5_128 = Hashing.md5();
   private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

   public RandomSupport() {
      super();
   }

   @VisibleForTesting
   public static long mixStafford13(long var0) {
      var0 = (var0 ^ var0 >>> 30) * -4658895280553007687L;
      var0 = (var0 ^ var0 >>> 27) * -7723592293110705685L;
      return var0 ^ var0 >>> 31;
   }

   public static Seed128bit upgradeSeedTo128bitUnmixed(long var0) {
      long var2 = var0 ^ 7640891576956012809L;
      long var4 = var2 + -7046029254386353131L;
      return new Seed128bit(var2, var4);
   }

   public static Seed128bit upgradeSeedTo128bit(long var0) {
      return upgradeSeedTo128bitUnmixed(var0).mixed();
   }

   public static Seed128bit seedFromHashOf(String var0) {
      byte[] var1 = MD5_128.hashString(var0, Charsets.UTF_8).asBytes();
      long var2 = Longs.fromBytes(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5], var1[6], var1[7]);
      long var4 = Longs.fromBytes(var1[8], var1[9], var1[10], var1[11], var1[12], var1[13], var1[14], var1[15]);
      return new Seed128bit(var2, var4);
   }

   public static long generateUniqueSeed() {
      return SEED_UNIQUIFIER.updateAndGet((var0) -> {
         return var0 * 1181783497276652981L;
      }) ^ System.nanoTime();
   }

   public static record Seed128bit(long seedLo, long seedHi) {
      public Seed128bit(long var1, long var3) {
         super();
         this.seedLo = var1;
         this.seedHi = var3;
      }

      public Seed128bit xor(long var1, long var3) {
         return new Seed128bit(this.seedLo ^ var1, this.seedHi ^ var3);
      }

      public Seed128bit xor(Seed128bit var1) {
         return this.xor(var1.seedLo, var1.seedHi);
      }

      public Seed128bit mixed() {
         return new Seed128bit(RandomSupport.mixStafford13(this.seedLo), RandomSupport.mixStafford13(this.seedHi));
      }

      public long seedLo() {
         return this.seedLo;
      }

      public long seedHi() {
         return this.seedHi;
      }
   }
}
