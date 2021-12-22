package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> MONUMENT_ENEMIES;

   public OceanMonumentFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(OceanMonumentFeature::checkLocation, OceanMonumentFeature::generatePieces));
   }

   protected boolean linearSeparation() {
      return false;
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> var0) {
      int var1 = var0.chunkPos().getBlockX(9);
      int var2 = var0.chunkPos().getBlockZ(9);
      Set var3 = var0.biomeSource().getBiomesWithin(var1, var0.chunkGenerator().getSeaLevel(), var2, 29, var0.chunkGenerator().climateSampler());
      Iterator var4 = var3.iterator();

      Biome var5;
      do {
         if (!var4.hasNext()) {
            return var0.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
         }

         var5 = (Biome)var4.next();
      } while(var5.getBiomeCategory() == Biome.BiomeCategory.OCEAN || var5.getBiomeCategory() == Biome.BiomeCategory.RIVER);

      return false;
   }

   private static StructurePiece createTopPiece(ChunkPos var0, WorldgenRandom var1) {
      int var2 = var0.getMinBlockX() - 29;
      int var3 = var0.getMinBlockZ() - 29;
      Direction var4 = Direction.Plane.HORIZONTAL.getRandomDirection(var1);
      return new OceanMonumentPieces.MonumentBuilding(var1, var2, var3, var4);
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<NoneFeatureConfiguration> var1) {
      var0.addPiece(createTopPiece(var1.chunkPos(), var1.random()));
   }

   public static PiecesContainer regeneratePiecesAfterLoad(ChunkPos var0, long var1, PiecesContainer var3) {
      if (var3.isEmpty()) {
         return var3;
      } else {
         WorldgenRandom var4 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
         var4.setLargeFeatureSeed(var1, var0.field_504, var0.field_505);
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

   static {
      MONUMENT_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4)));
   }
}
