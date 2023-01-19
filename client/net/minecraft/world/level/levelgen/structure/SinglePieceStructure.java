package net.minecraft.world.level.levelgen.structure;

import java.util.Optional;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public abstract class SinglePieceStructure extends Structure {
   private final SinglePieceStructure.PieceConstructor constructor;
   private int width;
   private int depth;

   protected SinglePieceStructure(SinglePieceStructure.PieceConstructor var1, int var2, int var3, Structure.StructureSettings var4) {
      super(var4);
      this.constructor = var1;
      this.width = var2;
      this.depth = var3;
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return getLowestY(var1, this.width, this.depth) < var1.chunkGenerator().getSeaLevel()
         ? Optional.empty()
         : onTopOfChunkCenter(var1, Heightmap.Types.WORLD_SURFACE_WG, var2 -> this.generatePieces(var2, var1));
   }

   private void generatePieces(StructurePiecesBuilder var1, Structure.GenerationContext var2) {
      ChunkPos var3 = var2.chunkPos();
      var1.addPiece(this.constructor.construct(var2.random(), var3.getMinBlockX(), var3.getMinBlockZ()));
   }

   @FunctionalInterface
   protected interface PieceConstructor {
      StructurePiece construct(WorldgenRandom var1, int var2, int var3);
   }
}
