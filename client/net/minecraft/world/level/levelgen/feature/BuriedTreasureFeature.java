package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureConfiguration> {
   public BuriedTreasureFeature(Function<Dynamic<?>, ? extends BuriedTreasureConfiguration> var1) {
      super(var1);
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      Biome var5 = var1.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
      if (var1.isBiomeValidStartForStructure(var5, Feature.BURIED_TREASURE)) {
         ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var3, var4, 10387320);
         BuriedTreasureConfiguration var6 = (BuriedTreasureConfiguration)var1.getStructureConfiguration(var5, Feature.BURIED_TREASURE);
         return var2.nextFloat() < var6.probability;
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
      public BuriedTreasureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
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
