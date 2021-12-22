package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class StrongholdFeature extends NoiseAffectingStructureFeature<NoneFeatureConfiguration> {
   public StrongholdFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(StrongholdFeature::checkLocation, StrongholdFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> var0) {
      return var0.chunkGenerator().hasStronghold(var0.chunkPos());
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      int var2 = 0;

      StrongholdPieces.StartPiece var3;
      do {
         var0.clear();
         var1.random().setLargeFeatureSeed(var1.seed() + (long)(var2++), var1.chunkPos().field_504, var1.chunkPos().field_505);
         StrongholdPieces.resetPieces();
         var3 = new StrongholdPieces.StartPiece(var1.random(), var1.chunkPos().getBlockX(2), var1.chunkPos().getBlockZ(2));
         var0.addPiece(var3);
         var3.addChildren(var3, var0, var1.random());
         List var4 = var3.pendingChildren;

         while(!var4.isEmpty()) {
            int var5 = var1.random().nextInt(var4.size());
            StructurePiece var6 = (StructurePiece)var4.remove(var5);
            var6.addChildren(var3, var0, var1.random());
         }

         var0.moveBelowSeaLevel(var1.chunkGenerator().getSeaLevel(), var1.chunkGenerator().getMinY(), var1.random(), 10);
      } while(var0.isEmpty() || var3.portalRoomPiece == null);

   }
}
