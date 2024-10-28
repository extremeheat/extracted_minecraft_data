package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BuriedTreasureStructure extends Structure {
   public static final MapCodec<BuriedTreasureStructure> CODEC = simpleCodec(BuriedTreasureStructure::new);

   public BuriedTreasureStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return onTopOfChunkCenter(var1, Heightmap.Types.OCEAN_FLOOR_WG, (var1x) -> {
         generatePieces(var1x, var1);
      });
   }

   private static void generatePieces(StructurePiecesBuilder var0, Structure.GenerationContext var1) {
      BlockPos var2 = new BlockPos(var1.chunkPos().getBlockX(9), 90, var1.chunkPos().getBlockZ(9));
      var0.addPiece(new BuriedTreasurePieces.BuriedTreasurePiece(var2));
   }

   public StructureType<?> type() {
      return StructureType.BURIED_TREASURE;
   }
}
