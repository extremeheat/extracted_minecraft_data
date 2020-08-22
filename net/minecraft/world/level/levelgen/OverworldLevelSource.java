package net.minecraft.world.level.levelgen;

import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class OverworldLevelSource extends NoiseBasedChunkGenerator {
   private static final float[] BIOME_WEIGHTS = (float[])Util.make(new float[25], (var0) -> {
      for(int var1 = -2; var1 <= 2; ++var1) {
         for(int var2 = -2; var2 <= 2; ++var2) {
            float var3 = 10.0F / Mth.sqrt((float)(var1 * var1 + var2 * var2) + 0.2F);
            var0[var1 + 2 + (var2 + 2) * 5] = var3;
         }
      }

   });
   private final PerlinNoise depthNoise;
   private final boolean isAmplified;
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final PatrolSpawner patrolSpawner = new PatrolSpawner();
   private final CatSpawner catSpawner = new CatSpawner();
   private final VillageSiege villageSiege = new VillageSiege();

   public OverworldLevelSource(LevelAccessor var1, BiomeSource var2, OverworldGeneratorSettings var3) {
      super(var1, var2, 4, 8, 256, var3, true);
      this.random.consumeCount(2620);
      this.depthNoise = new PerlinNoise(this.random, 15, 0);
      this.isAmplified = var1.getLevelData().getGeneratorType() == LevelType.AMPLIFIED;
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
      int var2 = var1.getCenterX();
      int var3 = var1.getCenterZ();
      Biome var4 = var1.getBiome((new ChunkPos(var2, var3)).getWorldPosition());
      WorldgenRandom var5 = new WorldgenRandom();
      var5.setDecorationSeed(var1.getSeed(), var2 << 4, var3 << 4);
      NaturalSpawner.spawnMobsForChunkGeneration(var1, var4, var2, var3, var5);
   }

   protected void fillNoiseColumn(double[] var1, int var2, int var3) {
      double var4 = 684.4119873046875D;
      double var6 = 684.4119873046875D;
      double var8 = 8.555149841308594D;
      double var10 = 4.277574920654297D;
      boolean var12 = true;
      boolean var13 = true;
      this.fillNoiseColumn(var1, var2, var3, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
   }

   protected double getYOffset(double var1, double var3, int var5) {
      double var6 = 8.5D;
      double var8 = ((double)var5 - (8.5D + var1 * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / var3;
      if (var8 < 0.0D) {
         var8 *= 4.0D;
      }

      return var8;
   }

   protected double[] getDepthAndScale(int var1, int var2) {
      double[] var3 = new double[2];
      float var4 = 0.0F;
      float var5 = 0.0F;
      float var6 = 0.0F;
      boolean var7 = true;
      int var8 = this.getSeaLevel();
      float var9 = this.biomeSource.getNoiseBiome(var1, var8, var2).getDepth();

      for(int var10 = -2; var10 <= 2; ++var10) {
         for(int var11 = -2; var11 <= 2; ++var11) {
            Biome var12 = this.biomeSource.getNoiseBiome(var1 + var10, var8, var2 + var11);
            float var13 = var12.getDepth();
            float var14 = var12.getScale();
            if (this.isAmplified && var13 > 0.0F) {
               var13 = 1.0F + var13 * 2.0F;
               var14 = 1.0F + var14 * 4.0F;
            }

            float var15 = BIOME_WEIGHTS[var10 + 2 + (var11 + 2) * 5] / (var13 + 2.0F);
            if (var12.getDepth() > var9) {
               var15 /= 2.0F;
            }

            var4 += var14 * var15;
            var5 += var13 * var15;
            var6 += var15;
         }
      }

      var4 /= var6;
      var5 /= var6;
      var4 = var4 * 0.9F + 0.1F;
      var5 = (var5 * 4.0F - 1.0F) / 8.0F;
      var3[0] = (double)var5 + this.getRdepth(var1, var2);
      var3[1] = (double)var4;
      return var3;
   }

   private double getRdepth(int var1, int var2) {
      double var3 = this.depthNoise.getValue((double)(var1 * 200), 10.0D, (double)(var2 * 200), 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
      if (var3 < 0.0D) {
         var3 = -var3 * 0.3D;
      }

      var3 = var3 * 3.0D - 2.0D;
      if (var3 < 0.0D) {
         var3 /= 28.0D;
      } else {
         if (var3 > 1.0D) {
            var3 = 1.0D;
         }

         var3 /= 40.0D;
      }

      return var3;
   }

   public List getMobsAt(MobCategory var1, BlockPos var2) {
      if (Feature.SWAMP_HUT.isSwamphut(this.level, var2)) {
         if (var1 == MobCategory.MONSTER) {
            return Feature.SWAMP_HUT.getSpecialEnemies();
         }

         if (var1 == MobCategory.CREATURE) {
            return Feature.SWAMP_HUT.getSpecialAnimals();
         }
      } else if (var1 == MobCategory.MONSTER) {
         if (Feature.PILLAGER_OUTPOST.isInsideBoundingFeature(this.level, var2)) {
            return Feature.PILLAGER_OUTPOST.getSpecialEnemies();
         }

         if (Feature.OCEAN_MONUMENT.isInsideBoundingFeature(this.level, var2)) {
            return Feature.OCEAN_MONUMENT.getSpecialEnemies();
         }
      }

      return super.getMobsAt(var1, var2);
   }

   public void tickCustomSpawners(ServerLevel var1, boolean var2, boolean var3) {
      this.phantomSpawner.tick(var1, var2, var3);
      this.patrolSpawner.tick(var1, var2, var3);
      this.catSpawner.tick(var1, var2, var3);
      this.villageSiege.tick(var1, var2, var3);
   }

   public int getSpawnHeight() {
      return this.level.getSeaLevel() + 1;
   }

   public int getSeaLevel() {
      return 63;
   }
}
