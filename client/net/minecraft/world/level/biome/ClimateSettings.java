package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClimateSettings(boolean hasPrecipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall) {
   public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.fieldOf("has_precipitation").forGetter((var0x) -> var0x.hasPrecipitation), Codec.FLOAT.fieldOf("temperature").forGetter((var0x) -> var0x.temperature), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((var0x) -> var0x.temperatureModifier), Codec.FLOAT.fieldOf("downfall").forGetter((var0x) -> var0x.downfall)).apply(var0, ClimateSettings::new));

   public ClimateSettings(boolean var1, float var2, Biome.TemperatureModifier var3, float var4) {
      super();
      this.hasPrecipitation = var1;
      this.temperature = var2;
      this.temperatureModifier = var3;
      this.downfall = var4;
   }

   Builder asBuilder() {
      return (new Builder()).hasPrecipitation(this.hasPrecipitation).temperature(this.temperature).temperatureAdjustment(this.temperatureModifier).downfall(this.downfall);
   }

   public static class Builder {
      private boolean hasPrecipitation;
      private float temperature;
      private Biome.TemperatureModifier temperatureModifier;
      private float downfall;

      public Builder() {
         super();
         this.temperatureModifier = Biome.TemperatureModifier.NONE;
      }

      public Builder hasPrecipitation(boolean var1) {
         this.hasPrecipitation = var1;
         return this;
      }

      public boolean hasPrecipitation() {
         return this.hasPrecipitation;
      }

      public Builder temperature(float var1) {
         this.temperature = var1;
         return this;
      }

      public float temperature() {
         return this.temperature;
      }

      public Builder temperatureAdjustment(Biome.TemperatureModifier var1) {
         this.temperatureModifier = var1;
         return this;
      }

      public Biome.TemperatureModifier temperatureModifier() {
         return this.temperatureModifier;
      }

      public Builder downfall(float var1) {
         this.downfall = var1;
         return this;
      }

      public float downfall() {
         return this.downfall;
      }

      ClimateSettings build() {
         return new ClimateSettings(this.hasPrecipitation, this.temperature, this.temperatureModifier, this.downfall);
      }
   }
}
