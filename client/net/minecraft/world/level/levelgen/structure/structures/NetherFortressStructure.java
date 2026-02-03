package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressStructure extends Structure {
   public static final WeightedList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES;
   public static final MapCodec<NetherFortressStructure> CODEC;

   public NetherFortressStructure(final Structure.StructureSettings settings) {
      super(settings);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(final Structure.GenerationContext context) {
      ChunkPos chunkPos = context.chunkPos();
      BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), 64, chunkPos.getMinBlockZ());
      return Optional.of(new Structure.GenerationStub(startPos, (builder) -> generatePieces(builder, context)));
   }

   private static void generatePieces(final StructurePiecesBuilder builder, final Structure.GenerationContext context) {
      NetherFortressPieces.StartPiece start = new NetherFortressPieces.StartPiece(context.random(), context.chunkPos().getBlockX(2), context.chunkPos().getBlockZ(2));
      builder.addPiece(start);
      start.addChildren(start, builder, context.random());
      List<StructurePiece> pendingChildren = start.pendingChildren;

      while(!pendingChildren.isEmpty()) {
         int pos = context.random().nextInt(pendingChildren.size());
         StructurePiece structurePiece = (StructurePiece)pendingChildren.remove(pos);
         structurePiece.addChildren(start, builder, context.random());
      }

      builder.moveInsideHeights(context.random(), 48, 70);
   }

   public StructureType<?> type() {
      return StructureType.FORTRESS;
   }

   static {
      FORTRESS_ENEMIES = WeightedList.<MobSpawnSettings.SpawnerData>builder().add(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 2, 3), 10).add(new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 4, 4), 5).add(new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 5, 5), 8).add(new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 5, 5), 2).add(new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 4, 4), 3).build();
      CODEC = simpleCodec(NetherFortressStructure::new);
   }
}
