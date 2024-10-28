package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class StrongholdStructure extends Structure {
   public static final MapCodec<StrongholdStructure> CODEC = simpleCodec(StrongholdStructure::new);

   public StrongholdStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return Optional.of(new Structure.GenerationStub(var1.chunkPos().getWorldPosition(), (var1x) -> {
         generatePieces(var1x, var1);
      }));
   }

   private static void generatePieces(StructurePiecesBuilder var0, Structure.GenerationContext var1) {
      int var2 = 0;

      StrongholdPieces.StartPiece var3;
      do {
         var0.clear();
         var1.random().setLargeFeatureSeed(var1.seed() + (long)(var2++), var1.chunkPos().x, var1.chunkPos().z);
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

   public StructureType<?> type() {
      return StructureType.STRONGHOLD;
   }
}
