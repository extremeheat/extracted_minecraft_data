package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.Holder;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;

public class BiomeManager implements BiomeResolver, NoiseBiomeResolver {
   public static final int HALF_QUART = 2;
   private final NoiseBiomeResolver noiseBiomeSource;
   private final long biomeZoomSeed;

   public BiomeManager(final NoiseBiomeResolver noiseBiomeSource, final long seed) {
      super();
      this.noiseBiomeSource = noiseBiomeSource;
      this.biomeZoomSeed = seed;
   }

   public static long obfuscateSeed(final long seed) {
      return Hashing.sha256().hashLong(seed).asLong();
   }

   public Holder<Biome> getBiome(final int x, final int y, final int z) {
      int absX = x - 2;
      int absY = y - 2;
      int absZ = z - 2;
      int parentX = absX >> 2;
      int parentY = absY >> 2;
      int parentZ = absZ >> 2;
      double fractX = (double)(absX & 3) / 4.0;
      double fractY = (double)(absY & 3) / 4.0;
      double fractZ = (double)(absZ & 3) / 4.0;
      int minI = 0;
      double minFiddledDistance = 1.0 / 0.0;

      for(int i = 0; i < 8; ++i) {
         boolean xEven = (i & 4) == 0;
         boolean yEven = (i & 2) == 0;
         boolean zEven = (i & 1) == 0;
         int cornerX = xEven ? parentX : parentX + 1;
         int cornerY = yEven ? parentY : parentY + 1;
         int cornerZ = zEven ? parentZ : parentZ + 1;
         double distanceX = xEven ? fractX : fractX - 1.0;
         double distanceY = yEven ? fractY : fractY - 1.0;
         double distanceZ = zEven ? fractZ : fractZ - 1.0;
         double next = getFiddledDistance(this.biomeZoomSeed, cornerX, cornerY, cornerZ, distanceX, distanceY, distanceZ);
         if (minFiddledDistance > next) {
            minI = i;
            minFiddledDistance = next;
         }
      }

      int biomeX = (minI & 4) == 0 ? parentX : parentX + 1;
      int biomeY = (minI & 2) == 0 ? parentY : parentY + 1;
      int biomeZ = (minI & 1) == 0 ? parentZ : parentZ + 1;
      return this.noiseBiomeSource.getNoiseBiome(biomeX, biomeY, biomeZ);
   }

   public Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.noiseBiomeSource.getNoiseBiome(quartX, quartY, quartZ);
   }

   private static double getFiddledDistance(final long seed, final int xRandom, final int yRandom, final int zRandom, final double distanceX, final double distanceY, final double distanceZ) {
      long fiddleSeed = prepareFiddleSeed(seed, xRandom, yRandom, zRandom);
      double fiddleX = getFiddle(fiddleSeed);
      fiddleSeed = nextFiddleSeed(seed, fiddleSeed);
      double fiddleY = getFiddle(fiddleSeed);
      fiddleSeed = nextFiddleSeed(seed, fiddleSeed);
      double fiddleZ = getFiddle(fiddleSeed);
      return Mth.square(distanceZ + fiddleZ) + Mth.square(distanceY + fiddleY) + Mth.square(distanceX + fiddleX);
   }

   protected static long prepareFiddleSeed(final long seed, final int xRandom, final int yRandom, final int zRandom) {
      long fiddleSeed = LinearCongruentialGenerator.next(seed, (long)xRandom);
      fiddleSeed = LinearCongruentialGenerator.next(fiddleSeed, (long)yRandom);
      fiddleSeed = LinearCongruentialGenerator.next(fiddleSeed, (long)zRandom);
      fiddleSeed = LinearCongruentialGenerator.next(fiddleSeed, (long)xRandom);
      fiddleSeed = LinearCongruentialGenerator.next(fiddleSeed, (long)yRandom);
      fiddleSeed = LinearCongruentialGenerator.next(fiddleSeed, (long)zRandom);
      return fiddleSeed;
   }

   protected static long nextFiddleSeed(final long seed, final long fiddleSeed) {
      return LinearCongruentialGenerator.next(fiddleSeed, seed);
   }

   protected static double getFiddle(final long rval) {
      double uniform = (double)Math.floorMod(rval >> 24, 1024) / 1024.0;
      return (uniform - 0.5) * 0.9;
   }
}
