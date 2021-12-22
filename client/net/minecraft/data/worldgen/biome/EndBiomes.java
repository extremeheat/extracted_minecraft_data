package net.minecraft.data.worldgen.biome;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class EndBiomes {
   public EndBiomes() {
      super();
   }

   private static Biome baseEndBiome(BiomeGenerationSettings.Builder var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.endSpawns(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.THEEND).temperature(0.5F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var0.build()).build();
   }

   public static Biome endBarrens() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      return baseEndBiome(var0);
   }

   public static Biome theEnd() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_SPIKE);
      return baseEndBiome(var0);
   }

   public static Biome endMidlands() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      return baseEndBiome(var0);
   }

   public static Biome endHighlands() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_GATEWAY_RETURN).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, EndPlacements.CHORUS_PLANT);
      return baseEndBiome(var0);
   }

   public static Biome smallEndIslands() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).addFeature(GenerationStep.Decoration.RAW_GENERATION, EndPlacements.END_ISLAND_DECORATED);
      return baseEndBiome(var0);
   }
}
