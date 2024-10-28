package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressStructure extends Structure {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES;
   public static final MapCodec<NetherFortressStructure> CODEC;

   public NetherFortressStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      ChunkPos var2 = var1.chunkPos();
      BlockPos var3 = new BlockPos(var2.getMinBlockX(), 64, var2.getMinBlockZ());
      return Optional.of(new Structure.GenerationStub(var3, (var1x) -> {
         generatePieces(var1x, var1);
      }));
   }

   private static void generatePieces(StructurePiecesBuilder var0, Structure.GenerationContext var1) {
      NetherFortressPieces.StartPiece var2 = new NetherFortressPieces.StartPiece(var1.random(), var1.chunkPos().getBlockX(2), var1.chunkPos().getBlockZ(2));
      var0.addPiece(var2);
      var2.addChildren(var2, var0, var1.random());
      List var3 = var2.pendingChildren;

      while(!var3.isEmpty()) {
         int var4 = var1.random().nextInt(var3.size());
         StructurePiece var5 = (StructurePiece)var3.remove(var4);
         var5.addChildren(var2, var0, var1.random());
      }

      var0.moveInsideHeights(var1.random(), 48, 70);
   }

   public StructureType<?> type() {
      return StructureType.FORTRESS;
   }

   static {
      FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)));
      CODEC = simpleCodec(NetherFortressStructure::new);
   }
}
