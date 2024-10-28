package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanMonumentStructure extends Structure {
   public static final MapCodec<OceanMonumentStructure> CODEC = simpleCodec(OceanMonumentStructure::new);

   public OceanMonumentStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      int var2 = var1.chunkPos().getBlockX(9);
      int var3 = var1.chunkPos().getBlockZ(9);
      Set var4 = var1.biomeSource().getBiomesWithin(var2, var1.chunkGenerator().getSeaLevel(), var3, 29, var1.randomState().sampler());
      Iterator var5 = var4.iterator();

      Holder var6;
      do {
         if (!var5.hasNext()) {
            return onTopOfChunkCenter(var1, Heightmap.Types.OCEAN_FLOOR_WG, (var1x) -> {
               generatePieces(var1x, var1);
            });
         }

         var6 = (Holder)var5.next();
      } while(var6.is(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING));

      return Optional.empty();
   }

   private static StructurePiece createTopPiece(ChunkPos var0, WorldgenRandom var1) {
      int var2 = var0.getMinBlockX() - 29;
      int var3 = var0.getMinBlockZ() - 29;
      Direction var4 = Direction.Plane.HORIZONTAL.getRandomDirection(var1);
      return new OceanMonumentPieces.MonumentBuilding(var1, var2, var3, var4);
   }

   private static void generatePieces(StructurePiecesBuilder var0, Structure.GenerationContext var1) {
      var0.addPiece(createTopPiece(var1.chunkPos(), var1.random()));
   }

   public static PiecesContainer regeneratePiecesAfterLoad(ChunkPos var0, long var1, PiecesContainer var3) {
      if (var3.isEmpty()) {
         return var3;
      } else {
         WorldgenRandom var4 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
         var4.setLargeFeatureSeed(var1, var0.x, var0.z);
         StructurePiece var5 = (StructurePiece)var3.pieces().get(0);
         BoundingBox var6 = var5.getBoundingBox();
         int var7 = var6.minX();
         int var8 = var6.minZ();
         Direction var9 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
         Direction var10 = (Direction)Objects.requireNonNullElse(var5.getOrientation(), var9);
         OceanMonumentPieces.MonumentBuilding var11 = new OceanMonumentPieces.MonumentBuilding(var4, var7, var8, var10);
         StructurePiecesBuilder var12 = new StructurePiecesBuilder();
         var12.addPiece(var11);
         return var12.build();
      }
   }

   public StructureType<?> type() {
      return StructureType.OCEAN_MONUMENT;
   }
}
