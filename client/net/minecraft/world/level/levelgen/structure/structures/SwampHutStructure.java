package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class SwampHutStructure extends Structure {
   public static final MapCodec<SwampHutStructure> CODEC = simpleCodec(SwampHutStructure::new);

   public SwampHutStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return onTopOfChunkCenter(var1, Heightmap.Types.WORLD_SURFACE_WG, (var1x) -> {
         generatePieces(var1x, var1);
      });
   }

   private static void generatePieces(StructurePiecesBuilder var0, Structure.GenerationContext var1) {
      var0.addPiece(new SwampHutPiece(var1.random(), var1.chunkPos().getMinBlockX(), var1.chunkPos().getMinBlockZ()));
   }

   public StructureType<?> type() {
      return StructureType.SWAMP_HUT;
   }
}
