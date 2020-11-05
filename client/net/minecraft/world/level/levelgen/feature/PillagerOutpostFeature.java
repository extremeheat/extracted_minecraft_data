package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

public class PillagerOutpostFeature extends JigsawFeature {
   private static final List<MobSpawnSettings.SpawnerData> OUTPOST_ENEMIES;

   public PillagerOutpostFeature(Codec<JigsawConfiguration> var1) {
      super(var1, 0, true, true);
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, JigsawConfiguration var10) {
      int var11 = SectionPos.blockToSectionCoord(var6);
      int var12 = SectionPos.blockToSectionCoord(var7);
      var5.setSeed((long)(var11 ^ var12 << 4) ^ var3);
      var5.nextInt();
      if (var5.nextInt(5) != 0) {
         return false;
      } else {
         return !this.isNearVillage(var1, var3, var5, var6, var7);
      }
   }

   private boolean isNearVillage(ChunkGenerator var1, long var2, WorldgenRandom var4, int var5, int var6) {
      StructureFeatureConfiguration var7 = var1.getSettings().getConfig(StructureFeature.VILLAGE);
      if (var7 == null) {
         return false;
      } else {
         for(int var8 = var5 - 10; var8 <= var5 + 10; ++var8) {
            for(int var9 = var6 - 10; var9 <= var6 + 10; ++var9) {
               ChunkPos var10 = StructureFeature.VILLAGE.getPotentialFeatureChunk(var7, var2, var4, var8, var9);
               if (var8 == var10.x && var9 == var10.z) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   static {
      OUTPOST_ENEMIES = ImmutableList.of(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1));
   }
}
