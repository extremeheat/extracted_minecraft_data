package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final List<MobSpawnSettings.SpawnerData> MONUMENT_ENEMIES;

   public OceanMonumentFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean linearSeparation() {
      return false;
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, NoneFeatureConfiguration var10) {
      Set var11 = var2.getBiomesWithin(SectionPos.sectionToBlockCoord(var6, 9), var1.getSeaLevel(), SectionPos.sectionToBlockCoord(var7, 9), 16);
      Iterator var12 = var11.iterator();

      Biome var13;
      do {
         if (!var12.hasNext()) {
            Set var15 = var2.getBiomesWithin(SectionPos.sectionToBlockCoord(var6, 9), var1.getSeaLevel(), SectionPos.sectionToBlockCoord(var7, 9), 29);
            Iterator var16 = var15.iterator();

            Biome var14;
            do {
               if (!var16.hasNext()) {
                  return true;
               }

               var14 = (Biome)var16.next();
            } while(var14.getBiomeCategory() == Biome.BiomeCategory.OCEAN || var14.getBiomeCategory() == Biome.BiomeCategory.RIVER);

            return false;
         }

         var13 = (Biome)var12.next();
      } while(var13.getGenerationSettings().isValidStart(this));

      return false;
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return OceanMonumentFeature.OceanMonumentStart::new;
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return MONUMENT_ENEMIES;
   }

   static {
      MONUMENT_ENEMIES = ImmutableList.of(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4));
   }

   public static class OceanMonumentStart extends StructureStart<NoneFeatureConfiguration> {
      private boolean isCreated;

      public OceanMonumentStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         this.generatePieces(var4, var5);
      }

      private void generatePieces(int var1, int var2) {
         int var3 = SectionPos.sectionToBlockCoord(var1) - 29;
         int var4 = SectionPos.sectionToBlockCoord(var2) - 29;
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
         this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, var3, var4, var5));
         this.calculateBoundingBox();
         this.isCreated = true;
      }

      public void placeInChunk(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6) {
         if (!this.isCreated) {
            this.pieces.clear();
            this.generatePieces(this.getChunkX(), this.getChunkZ());
         }

         super.placeInChunk(var1, var2, var3, var4, var5, var6);
      }
   }
}
