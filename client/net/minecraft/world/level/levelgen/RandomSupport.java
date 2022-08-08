package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
   public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   public static final long SILVER_RATIO_64 = 7640891576956012809L;
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

   public static Seed128bit upgradeSeedTo128bit(long var0) {
      long var2 = var0 ^ 7640891576956012809L;
      long var4 = var2 + -7046029254386353131L;
      return new Seed128bit(mixStafford13(var2), mixStafford13(var4));
   }

   public static long generateUniqueSeed() {
      return SEED_UNIQUIFIER.updateAndGet((var0) -> {
         return var0 * 1181783497276652981L;
      }) ^ System.nanoTime();
   }

   public static record Seed128bit(long a, long b) {
      private final long seedLo;
      private final long seedHi;

      public Seed128bit(long var1, long var3) {
         super();
         this.seedLo = var1;
         this.seedHi = var3;
      }

      public long seedLo() {
         return this.seedLo;
      }

      public long seedHi() {
         return this.seedHi;
      }
   }
}
