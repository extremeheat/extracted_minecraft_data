package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final List<MobSpawnSettings.SpawnerData> SWAMPHUT_ENEMIES;
   private static final List<MobSpawnSettings.SpawnerData> SWAMPHUT_ANIMALS;

   public SwamplandHutFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return SwamplandHutFeature.FeatureStart::new;
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return SWAMPHUT_ENEMIES;
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialAnimals() {
      return SWAMPHUT_ANIMALS;
   }

   static {
      SWAMPHUT_ENEMIES = ImmutableList.of(new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1));
      SWAMPHUT_ANIMALS = ImmutableList.of(new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1));
   }

   public static class FeatureStart extends StructureStart<NoneFeatureConfiguration> {
      public FeatureStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         SwamplandHutPiece var8 = new SwamplandHutPiece(this.random, var4 * 16, var5 * 16);
         this.pieces.add(var8);
         this.calculateBoundingBox();
      }
   }
}
