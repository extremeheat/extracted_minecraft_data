package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class DesertPyramidFeature extends StructureFeature<NoneFeatureConfiguration> {
   public DesertPyramidFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(DesertPyramidFeature::checkLocation, DesertPyramidFeature::generatePieces));
   }

   private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> var0) {
      if (!var0.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
         return false;
      } else {
         return var0.getLowestY(21, 21) >= var0.chunkGenerator().getSeaLevel();
      }
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      var0.addPiece(new DesertPyramidPiece(var1.random(), var1.chunkPos().getMinBlockX(), var1.chunkPos().getMinBlockZ()));
   }
}
