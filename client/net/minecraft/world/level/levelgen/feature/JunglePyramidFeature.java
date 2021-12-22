package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class JunglePyramidFeature extends StructureFeature<NoneFeatureConfiguration> {
   public JunglePyramidFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(JunglePyramidFeature::checkLocation, JunglePyramidFeature::generatePieces));
   }

   private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> var0) {
      if (!var0.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
         return false;
      } else {
         return var0.getLowestY(12, 15) >= var0.chunkGenerator().getSeaLevel();
      }
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      var0.addPiece(new JunglePyramidPiece(var1.random(), var1.chunkPos().getMinBlockX(), var1.chunkPos().getMinBlockZ()));
   }
}
