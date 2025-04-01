package net.minecraft.world.level.biome;

import java.util.Objects;
import javax.annotation.Nullable;

public class BiomeBuilder {
   @Nullable
   private ClimateSettings.Builder climateSettings;
   @Nullable
   private BiomeSpecialEffects.Builder specialEffects;
   @Nullable
   private MobSpawnSettings.Builder mobSpawnSettings;
   @Nullable
   private BiomeGenerationSettings.PlainBuilder generationSettings;

   public BiomeBuilder() {
      super();
   }

   public BiomeBuilder climate(ClimateSettings.Builder var1) {
      this.climateSettings = var1;
      return this;
   }

   public ClimateSettings.Builder climateSettings() {
      return (ClimateSettings.Builder)Objects.requireNonNull(this.climateSettings);
   }

   public BiomeBuilder specialEffects(BiomeSpecialEffects.Builder var1) {
      this.specialEffects = var1;
      return this;
   }

   public BiomeSpecialEffects.Builder specialEffects() {
      return (BiomeSpecialEffects.Builder)Objects.requireNonNull(this.specialEffects);
   }

   public BiomeBuilder mobSpawnSettings(MobSpawnSettings.Builder var1) {
      this.mobSpawnSettings = var1;
      return this;
   }

   public MobSpawnSettings.Builder mobSpawnSettings() {
      return (MobSpawnSettings.Builder)Objects.requireNonNull(this.mobSpawnSettings);
   }

   public BiomeBuilder generationSettings(BiomeGenerationSettings.PlainBuilder var1) {
      this.generationSettings = var1;
      return this;
   }

   public BiomeGenerationSettings.PlainBuilder generationSettings() {
      return (BiomeGenerationSettings.PlainBuilder)Objects.requireNonNull(this.generationSettings);
   }

   public Biome build() {
      if (this.climateSettings != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
         return new Biome(this.climateSettings.build(), this.specialEffects.build(), this.generationSettings.build(), this.mobSpawnSettings.build());
      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome\n" + String.valueOf(this));
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.climateSettings);
      return "BiomeBuilder{\nclimateSettings=" + var10000 + ",\nspecialEffects=" + String.valueOf(this.specialEffects) + ",\nmobSpawnSettings=" + String.valueOf(this.mobSpawnSettings) + ",\ngenerationSettings=" + String.valueOf(this.generationSettings) + ",\n}";
   }
}
