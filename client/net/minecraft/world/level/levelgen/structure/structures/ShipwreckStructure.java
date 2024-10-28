package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
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
      ShipwreckPieces.addPieces(var2.structureTemplateManager(), var4, var3, var1, var2.random(), this.isBeached);
   }

   public StructureType<?> type() {
      return StructureType.SHIPWRECK;
   }
}
