package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class ShipwreckStructure extends Structure {
   public static final MapCodec<ShipwreckStructure> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(settingsCodec(var0), Codec.BOOL.fieldOf("is_beached").forGetter((var0x) -> {
         return var0x.isBeached;
      })).apply(var0, ShipwreckStructure::new);
   });
   public final boolean isBeached;

   public ShipwreckStructure(Structure.StructureSettings var1, boolean var2) {
      super(var1);
      this.isBeached = var2;
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      Heightmap.Types var2 = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
      return onTopOfChunkCenter(var1, var2, (var2x) -> {
         this.generatePieces(var2x, var1);
      });
   }

   private void generatePieces(StructurePiecesBuilder var1, Structure.GenerationContext var2) {
      Rotation var3 = Rotation.getRandom(var2.random());
      BlockPos var4 = new BlockPos(var2.chunkPos().getMinBlockX(), 90, var2.chunkPos().getMinBlockZ());
      ShipwreckPieces.ShipwreckPiece var5 = ShipwreckPieces.addRandomPiece(var2.structureTemplateManager(), var4, var3, var1, var2.random(), this.isBeached);
      if (var5.isTooBigToFitInWorldGenRegion()) {
         BoundingBox var6 = var5.getBoundingBox();
         int var7;
         if (this.isBeached) {
            int var8 = Structure.getLowestY(var2, var6.minX(), var6.getXSpan(), var6.minZ(), var6.getZSpan());
            var7 = var5.calculateBeachedPosition(var8, var2.random());
         } else {
            var7 = Structure.getMeanFirstOccupiedHeight(var2, var6.minX(), var6.getXSpan(), var6.minZ(), var6.getZSpan());
         }

         var5.adjustPositionHeight(var7);
      }

   }

   public StructureType<?> type() {
      return StructureType.SHIPWRECK;
   }
}
