package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityFeatureConfiguration> {
   private static final int RANDOM_SALT = 10387320;

   public BuriedTreasureFeature(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(BuriedTreasureFeature::checkLocation, BuriedTreasureFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<ProbabilityFeatureConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureWithSalt(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505, 10387320);
      return var1.nextFloat() < ((ProbabilityFeatureConfiguration)var0.config()).probability && var0.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<ProbabilityFeatureConfiguration> var1) {
      BlockPos var2 = new BlockPos(var1.chunkPos().getBlockX(9), 90, var1.chunkPos().getBlockZ(9));
      var0.addPiece(new BuriedTreasurePieces.BuriedTreasurePiece(var2));
   }

   public BlockPos getLocatePos(ChunkPos var1) {
      return new BlockPos(var1.getBlockX(9), 0, var1.getBlockZ(9));
   }
}
