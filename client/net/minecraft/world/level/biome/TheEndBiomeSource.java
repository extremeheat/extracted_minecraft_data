package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
   private final SimplexNoise islandNoise;
   private final WorldgenRandom random;
   private final Biome[] possibleBiomes;

   public TheEndBiomeSource(TheEndBiomeSourceSettings var1) {
      super();
      this.possibleBiomes = new Biome[]{Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};
      this.random = new WorldgenRandom(var1.getSeed());
      this.random.consumeCount(17292);
      this.islandNoise = new SimplexNoise(this.random);
   }

   public Biome getBiome(int var1, int var2) {
      int var3 = var1 >> 4;
      int var4 = var2 >> 4;
      if ((long)var3 * (long)var3 + (long)var4 * (long)var4 <= 4096L) {
         return Biomes.THE_END;
      } else {
         float var5 = this.getHeightValue(var3 * 2 + 1, var4 * 2 + 1);
         if (var5 > 40.0F) {
            return Biomes.END_HIGHLANDS;
         } else if (var5 >= 0.0F) {
            return Biomes.END_MIDLANDS;
         } else {
            return var5 < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
         }
      }
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];
      Long2ObjectOpenHashMap var7 = new Long2ObjectOpenHashMap();

      for(int var8 = 0; var8 < var3; ++var8) {
         for(int var9 = 0; var9 < var4; ++var9) {
            int var10 = var8 + var1;
            int var11 = var9 + var2;
            long var12 = ChunkPos.asLong(var10, var11);
            Biome var14 = (Biome)var7.get(var12);
            if (var14 == null) {
               var14 = this.getBiome(var10, var11);
               var7.put(var12, var14);
            }

            var6[var8 + var9 * var3] = var14;
         }
      }

      return var6;
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3) {
      int var4 = var1 - var3 >> 2;
      int var5 = var2 - var3 >> 2;
      int var6 = var1 + var3 >> 2;
      int var7 = var2 + var3 >> 2;
      int var8 = var6 - var4 + 1;
      int var9 = var7 - var5 + 1;
      return Sets.newHashSet(this.getBiomeBlock(var4, var5, var8, var9));
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      int var6 = var1 - var3 >> 2;
      int var7 = var2 - var3 >> 2;
      int var8 = var1 + var3 >> 2;
      int var9 = var2 + var3 >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      Biome[] var12 = this.getBiomeBlock(var6, var7, var10, var11);
      BlockPos var13 = null;
      int var14 = 0;

      for(int var15 = 0; var15 < var10 * var11; ++var15) {
         int var16 = var6 + var15 % var10 << 2;
         int var17 = var7 + var15 / var10 << 2;
         if (var4.contains(var12[var15])) {
            if (var13 == null || var5.nextInt(var14 + 1) == 0) {
               var13 = new BlockPos(var16, 0, var17);
            }

            ++var14;
         }
      }

      return var13;
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

   public boolean canGenerateStructure(StructureFeature<?> var1) {
      return (Boolean)this.supportedStructures.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.possibleBiomes;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Biome var5 = var2[var4];
            if (var5.isValidStart(var1x)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         Biome[] var1 = this.possibleBiomes;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.surfaceBlocks.add(var4.getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }
}
