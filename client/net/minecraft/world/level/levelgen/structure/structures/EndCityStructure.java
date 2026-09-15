package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class EndCityStructure extends Structure {
   public static final MapCodec<EndCityStructure> CODEC = simpleCodec(EndCityStructure::new);
   private static final int START_OFFSET_IN_CHUNK = 7;

   public EndCityStructure(final Structure.StructureSettings settings) {
      super(settings);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(final Structure.GenerationContext context) {
      ChunkPos chunkPos = context.chunkPos();
      int blockX = chunkPos.getBlockX(7);
      int blockZ = chunkPos.getBlockZ(7);
      if (!context.couldValidBiomeExistInTerrainColumn(blockX, blockZ)) {
         return Optional.empty();
      } else {
         Rotation rotation = Rotation.getRandom(context.random());
         BlockPos startPos = this.getLowestYIn5by5Box(context, blockX, blockZ, rotation);
         return startPos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(startPos, (builder) -> this.generatePieces(builder, startPos, rotation, context)));
      }
   }

   private void generatePieces(final StructurePiecesBuilder builder, final BlockPos startPos, final Rotation rotation, final Structure.GenerationContext context) {
      List<StructurePiece> pieces = Lists.newArrayList();
      EndCityPieces.startHouseTower(context.structureTemplateManager(), startPos, rotation, pieces, context.random());
      Objects.requireNonNull(builder);
      pieces.forEach(builder::addPiece);
   }

   public StructureType<?> type() {
      return StructureType.END_CITY;
   }
}
