package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EndCityFeature extends StructureFeature<NoneFeatureConfiguration> {
   public EndCityFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.getSettings().getEndCitySpacing();
      int var8 = var1.getSettings().getEndCitySeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var13, var14, 10387313);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.x && var4 == var5.z) {
         Biome var6 = var1.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
         if (!var1.isBiomeValidStartForStructure(var6, Feature.END_CITY)) {
            return false;
         } else {
            int var7 = getYPositionForFeature(var3, var4, var1);
            return var7 >= 60;
         }
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return EndCityFeature.EndCityStart::new;
   }

   public String getFeatureName() {
      return "EndCity";
   }

   public int getLookupRange() {
      return 8;
   }

   private static int getYPositionForFeature(int var0, int var1, ChunkGenerator<?> var2) {
      Random var3 = new Random((long)(var0 + var1 * 10387313));
      Rotation var4 = Rotation.values()[var3.nextInt(Rotation.values().length)];
      byte var5 = 5;
      byte var6 = 5;
      if (var4 == Rotation.CLOCKWISE_90) {
         var5 = -5;
      } else if (var4 == Rotation.CLOCKWISE_180) {
         var5 = -5;
         var6 = -5;
      } else if (var4 == Rotation.COUNTERCLOCKWISE_90) {
         var6 = -5;
      }

      int var7 = (var0 << 4) + 7;
      int var8 = (var1 << 4) + 7;
      int var9 = var2.getFirstOccupiedHeight(var7, var8, Heightmap.Types.WORLD_SURFACE_WG);
      int var10 = var2.getFirstOccupiedHeight(var7, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG);
      int var11 = var2.getFirstOccupiedHeight(var7 + var5, var8, Heightmap.Types.WORLD_SURFACE_WG);
      int var12 = var2.getFirstOccupiedHeight(var7 + var5, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG);
      return Math.min(Math.min(var9, var10), Math.min(var11, var12));
   }

   public static class EndCityStart extends StructureStart {
      public EndCityStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         Rotation var6 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         int var7 = EndCityFeature.getYPositionForFeature(var3, var4, var1);
         if (var7 >= 60) {
            BlockPos var8 = new BlockPos(var3 * 16 + 8, var7, var4 * 16 + 8);
            EndCityPieces.startHouseTower(var2, var8, var6, this.pieces, this.random);
            this.calculateBoundingBox();
         }
      }
   }
}
