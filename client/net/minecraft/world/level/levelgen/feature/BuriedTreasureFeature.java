package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityFeatureConfiguration> {
   public BuriedTreasureFeature(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, ProbabilityFeatureConfiguration var10) {
      var5.setLargeFeatureWithSalt(var3, var6, var7, 10387320);
      return var5.nextFloat() < var10.probability;
   }

   public StructureFeature.StructureStartFactory<ProbabilityFeatureConfiguration> getStartFactory() {
      return BuriedTreasureFeature.BuriedTreasureStart::new;
   }

   public static class BuriedTreasureStart extends StructureStart<ProbabilityFeatureConfiguration> {
      public BuriedTreasureStart(StructureFeature<ProbabilityFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, ProbabilityFeatureConfiguration var7) {
         BlockPos var8 = new BlockPos(SectionPos.sectionToBlockCoord(var4, 9), 90, SectionPos.sectionToBlockCoord(var5, 9));
         this.pieces.add(new BuriedTreasurePieces.BuriedTreasurePiece(var8));
         this.calculateBoundingBox();
      }

      public BlockPos getLocatePos() {
         return new BlockPos(SectionPos.sectionToBlockCoord(this.getChunkX(), 9), 0, SectionPos.sectionToBlockCoord(this.getChunkZ(), 9));
      }
   }
}
