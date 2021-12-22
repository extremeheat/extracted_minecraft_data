package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.QuartPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressFeature extends StructureFeature<NoneFeatureConfiguration> {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES;

   public NetherFortressFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(NetherFortressFeature::checkLocation, NetherFortressFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      return var1.nextInt(5) >= 2 ? false : var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var0.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(64), QuartPos.fromBlock(var0.chunkPos().getMiddleBlockZ())));
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      NetherBridgePieces.StartPiece var2 = new NetherBridgePieces.StartPiece(var1.random(), var1.chunkPos().getBlockX(2), var1.chunkPos().getBlockZ(2));
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

   static {
      FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)));
   }
}
