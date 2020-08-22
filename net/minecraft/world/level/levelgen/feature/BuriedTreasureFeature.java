package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class BuriedTreasureFeature extends StructureFeature {
   public BuriedTreasureFeature(Function var1) {
      super(var1);
   }

   public boolean isFeatureChunk(BiomeManager var1, ChunkGenerator var2, Random var3, int var4, int var5, Biome var6) {
      if (var2.isBiomeValidStartForStructure(var6, this)) {
         ((WorldgenRandom)var3).setLargeFeatureWithSalt(var2.getSeed(), var4, var5, 10387320);
         BuriedTreasureConfiguration var7 = (BuriedTreasureConfiguration)var2.getStructureConfiguration(var6, this);
         return var3.nextFloat() < var7.probability;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return BuriedTreasureFeature.BuriedTreasureStart::new;
   }

   public String getFeatureName() {
      return "Buried_Treasure";
   }

   public int getLookupRange() {
      return 1;
   }

   public static class BuriedTreasureStart extends StructureStart {
      public BuriedTreasureStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         int var6 = var3 * 16;
         int var7 = var4 * 16;
         BlockPos var8 = new BlockPos(var6 + 9, 90, var7 + 9);
         this.pieces.add(new BuriedTreasurePieces.BuriedTreasurePiece(var8));
         this.calculateBoundingBox();
      }

      public BlockPos getLocatePos() {
         return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
      }
   }
}
