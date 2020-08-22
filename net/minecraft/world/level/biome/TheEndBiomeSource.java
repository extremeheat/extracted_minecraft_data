package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
   private final SimplexNoise islandNoise;
   private final WorldgenRandom random;
   private static final Set POSSIBLE_BIOMES;

   public TheEndBiomeSource(TheEndBiomeSourceSettings var1) {
      super(POSSIBLE_BIOMES);
      this.random = new WorldgenRandom(var1.getSeed());
      this.random.consumeCount(17292);
      this.islandNoise = new SimplexNoise(this.random);
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      int var4 = var1 >> 2;
      int var5 = var3 >> 2;
      if ((long)var4 * (long)var4 + (long)var5 * (long)var5 <= 4096L) {
         return Biomes.THE_END;
      } else {
         float var6 = this.getHeightValue(var4 * 2 + 1, var5 * 2 + 1);
         if (var6 > 40.0F) {
            return Biomes.END_HIGHLANDS;
         } else if (var6 >= 0.0F) {
            return Biomes.END_MIDLANDS;
         } else {
            return var6 < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
         }
      }
   }

   public float getHeightValue(int var1, int var2) {
      int var3 = var1 / 2;
      int var4 = var2 / 2;
      int var5 = var1 % 2;
      int var6 = var2 % 2;
      float var7 = 100.0F - Mth.sqrt((float)(var1 * var1 + var2 * var2)) * 8.0F;
      var7 = Mth.clamp(var7, -100.0F, 80.0F);

      for(int var8 = -12; var8 <= 12; ++var8) {
         for(int var9 = -12; var9 <= 12; ++var9) {
            long var10 = (long)(var3 + var8);
            long var12 = (long)(var4 + var9);
            if (var10 * var10 + var12 * var12 > 4096L && this.islandNoise.getValue((double)var10, (double)var12) < -0.8999999761581421D) {
               float var14 = (Mth.abs((float)var10) * 3439.0F + Mth.abs((float)var12) * 147.0F) % 13.0F + 9.0F;
               float var15 = (float)(var5 - var8 * 2);
               float var16 = (float)(var6 - var9 * 2);
               float var17 = 100.0F - Mth.sqrt(var15 * var15 + var16 * var16) * var14;
               var17 = Mth.clamp(var17, -100.0F, 80.0F);
               var7 = Math.max(var7, var17);
            }
         }
      }

      return var7;
   }

   static {
      POSSIBLE_BIOMES = ImmutableSet.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
   }
}
