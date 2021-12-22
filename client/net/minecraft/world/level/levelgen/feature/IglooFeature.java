package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IglooFeature extends StructureFeature<NoneFeatureConfiguration> {
   public IglooFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG), IglooFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      BlockPos var2 = new BlockPos(var1.chunkPos().getMinBlockX(), 90, var1.chunkPos().getMinBlockZ());
      Rotation var3 = Rotation.getRandom(var1.random());
      IglooPieces.addPieces(var1.structureManager(), var2, var3, var0, var1.random());
   }
}
