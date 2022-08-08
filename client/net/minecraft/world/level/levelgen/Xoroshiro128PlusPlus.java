package net.minecraft.world.level.levelgen;

public class Xoroshiro128PlusPlus {
   private long seedLo;
   private long seedHi;

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
}
