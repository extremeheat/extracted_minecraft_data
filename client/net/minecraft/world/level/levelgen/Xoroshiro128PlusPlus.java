package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.stream.LongStream;
import net.minecraft.Util;

public class Xoroshiro128PlusPlus {
   private long seedLo;
   private long seedHi;
   public static final Codec<Xoroshiro128PlusPlus> CODEC;

   public Xoroshiro128PlusPlus(RandomSupport.Seed128bit var1) {
      this(var1.seedLo(), var1.seedHi());
   }

   public Xoroshiro128PlusPlus(long var1, long var3) {
      super();
      this.seedLo = var1;
      this.seedHi = var3;
      if ((this.seedLo | this.seedHi) == 0L) {
         this.seedLo = -7046029254386353131L;
         this.seedHi = 7640891576956012809L;
      }

   }

   public long nextLong() {
      long var1 = this.seedLo;
      long var3 = this.seedHi;
      long var5 = Long.rotateLeft(var1 + var3, 17) + var1;
      var3 ^= var1;
      this.seedLo = Long.rotateLeft(var1, 49) ^ var3 ^ var3 << 21;
      this.seedHi = Long.rotateLeft(var3, 28);
      return var5;
   }

   static {
      CODEC = Codec.LONG_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize((LongStream)var0, 2).map((var0x) -> {
            return new Xoroshiro128PlusPlus(var0x[0], var0x[1]);
         });
      }, (var0) -> {
         return LongStream.of(new long[]{var0.seedLo, var0.seedHi});
      });
   }
}
