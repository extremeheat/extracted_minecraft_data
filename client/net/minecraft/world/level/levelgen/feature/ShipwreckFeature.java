package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class ShipwreckFeature extends StructureFeature<ShipwreckConfiguration> {
   public ShipwreckFeature(Codec<ShipwreckConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(ShipwreckFeature::checkLocation, ShipwreckFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<ShipwreckConfiguration> var0) {
      Heightmap.Types var1 = ((ShipwreckConfiguration)var0.config()).isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
      return var0.validBiomeOnTop(var1);
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<ShipwreckConfiguration> var1) {
      Rotation var2 = Rotation.getRandom(var1.random());
      BlockPos var3 = new BlockPos(var1.chunkPos().getMinBlockX(), 90, var1.chunkPos().getMinBlockZ());
      ShipwreckPieces.addPieces(var1.structureManager(), var3, var2, var0, var1.random(), (ShipwreckConfiguration)var1.config());
   }
}
