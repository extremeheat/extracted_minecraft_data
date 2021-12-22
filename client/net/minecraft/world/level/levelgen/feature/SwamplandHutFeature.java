package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class SwamplandHutFeature extends StructureFeature<NoneFeatureConfiguration> {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> SWAMPHUT_ENEMIES;
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> SWAMPHUT_ANIMALS;

   public SwamplandHutFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG), SwamplandHutFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      var0.addPiece(new SwamplandHutPiece(var1.random(), var1.chunkPos().getMinBlockX(), var1.chunkPos().getMinBlockZ()));
   }

   static {
      SWAMPHUT_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1)));
      SWAMPHUT_ANIMALS = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1)));
   }
}
