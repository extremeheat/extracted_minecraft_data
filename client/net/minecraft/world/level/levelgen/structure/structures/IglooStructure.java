package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IglooStructure extends Structure {
   public static final MapCodec<IglooStructure> CODEC = simpleCodec(IglooStructure::new);

   public IglooStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return onTopOfChunkCenter(var1, Heightmap.Types.WORLD_SURFACE_WG, (var2) -> {
         this.generatePieces(var2, var1);
      });
   }

   private void generatePieces(StructurePiecesBuilder var1, Structure.GenerationContext var2) {
      ChunkPos var3 = var2.chunkPos();
      WorldgenRandom var4 = var2.random();
      BlockPos var5 = new BlockPos(var3.getMinBlockX(), 90, var3.getMinBlockZ());
      Rotation var6 = Rotation.getRandom(var4);
      IglooPieces.addPieces(var2.structureTemplateManager(), var5, var6, var1, var4);
   }

   public StructureType<?> type() {
      return StructureType.IGLOO;
   }
}
