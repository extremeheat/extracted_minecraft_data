package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.VillageConfiguration;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class VillageFeature extends StructureFeature {
   public VillageFeature(Function var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.getSettings().getVillagesSpacing();
      int var8 = var1.getSettings().getVillagesSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var13, var14, 10387312);
      var13 *= var7;
      var14 *= var7;
      var13 += var2.nextInt(var7 - var8);
      var14 += var2.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(BiomeManager var1, ChunkGenerator var2, Random var3, int var4, int var5, Biome var6) {
      ChunkPos var7 = this.getPotentialFeatureChunkFromLocationWithOffset(var2, var3, var4, var5, 0, 0);
      return var4 == var7.x && var5 == var7.z ? var2.isBiomeValidStartForStructure(var6, this) : false;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return VillageFeature.FeatureStart::new;
   }

   public String getFeatureName() {
      return "Village";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class FeatureStart extends BeardedStructureStart {
      public FeatureStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         VillageConfiguration var6 = (VillageConfiguration)var1.getStructureConfiguration(var5, Feature.VILLAGE);
         BlockPos var7 = new BlockPos(var3 * 16, 0, var4 * 16);
         VillagePieces.addPieces(var1, var2, var7, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}
