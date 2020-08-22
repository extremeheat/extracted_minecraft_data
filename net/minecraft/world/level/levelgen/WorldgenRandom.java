package net.minecraft.world.level.levelgen;

import java.util.Random;

public class WorldgenRandom extends Random {
   private int count;

   public WorldgenRandom() {
   }

   public WorldgenRandom(long var1) {
      super(var1);
   }

   public void consumeCount(int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         this.next(1);
      }

   }

   protected int next(int var1) {
      ++this.count;
      return super.next(var1);
   }

   public long setBaseChunkSeed(int var1, int var2) {
      long var3 = (long)var1 * 341873128712L + (long)var2 * 132897987541L;
      this.setSeed(var3);
      return var3;
   }

   public long setDecorationSeed(long var1, int var3, int var4) {
      this.setSeed(var1);
      long var5 = this.nextLong() | 1L;
      long var7 = this.nextLong() | 1L;
      long var9 = (long)var3 * var5 + (long)var4 * var7 ^ var1;
      this.setSeed(var9);
      return var9;
   }

   public long setFeatureSeed(long var1, int var3, int var4) {
      long var5 = var1 + (long)var3 + (long)(10000 * var4);
      this.setSeed(var5);
      return var5;
   }

   public long setLargeFeatureSeed(long var1, int var3, int var4) {
      this.setSeed(var1);
      long var5 = this.nextLong();
      long var7 = this.nextLong();
      long var9 = (long)var3 * var5 ^ (long)var4 * var7 ^ var1;
      this.setSeed(var9);
      return var9;
   }

   public long setLargeFeatureWithSalt(long var1, int var3, int var4, int var5) {
      long var6 = (long)var3 * 341873128712L + (long)var4 * 132897987541L + var1 + (long)var5;
      this.setSeed(var6);
      return var6;
   }

   public static Random seedSlimeChunk(int var0, int var1, long var2, long var4) {
      return new Random(var2 + (long)(var0 * var0 * 4987142) + (long)(var0 * 5947611) + (long)(var1 * var1) * 4392871L + (long)(var1 * 389711) ^ var4);
   }
}
