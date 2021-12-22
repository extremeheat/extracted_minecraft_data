package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class PillagerOutpostFeature extends JigsawFeature {
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> OUTPOST_ENEMIES;

   public PillagerOutpostFeature(Codec<JigsawConfiguration> var1) {
      super(var1, 0, true, true, PillagerOutpostFeature::checkLocation);
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> var0) {
      int var1 = var0.chunkPos().field_504 >> 4;
      int var2 = var0.chunkPos().field_505 >> 4;
      WorldgenRandom var3 = new WorldgenRandom(new LegacyRandomSource(0L));
      var3.setSeed((long)(var1 ^ var2 << 4) ^ var0.seed());
      var3.nextInt();
      if (var3.nextInt(5) != 0) {
         return false;
      } else {
         return !isNearVillage(var0.chunkGenerator(), var0.seed(), var0.chunkPos());
      }
   }

   private static boolean isNearVillage(ChunkGenerator var0, long var1, ChunkPos var3) {
      StructureFeatureConfiguration var4 = var0.getSettings().getConfig(StructureFeature.VILLAGE);
      if (var4 == null) {
         return false;
      } else {
         int var5 = var3.field_504;
         int var6 = var3.field_505;

         for(int var7 = var5 - 10; var7 <= var5 + 10; ++var7) {
            for(int var8 = var6 - 10; var8 <= var6 + 10; ++var8) {
               ChunkPos var9 = StructureFeature.VILLAGE.getPotentialFeatureChunk(var4, var1, var7, var8);
               if (var7 == var9.field_504 && var8 == var9.field_505) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   static {
      OUTPOST_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1)));
   }
}
