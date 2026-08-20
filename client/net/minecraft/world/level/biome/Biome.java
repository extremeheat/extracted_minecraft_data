package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.attribute.modifier.MobSpawnSettingsModifier;
import net.minecraft.world.level.DryFoliageColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NoiseStack;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public final class Biome {
   public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Biome.ClimateSettings.CODEC.forGetter((b) -> b.climateSettings), EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter((b) -> b.attributes), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((b) -> b.specialEffects), BiomeGenerationSettings.CODEC.forGetter((b) -> b.generationSettings)).apply(i, Biome::new));
   public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(Biome.ClimateSettings.CODEC.forGetter((b) -> b.climateSettings), EnvironmentAttributeMap.NETWORK_CODEC.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter((b) -> b.attributes), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((b) -> b.specialEffects)).apply(i, (climateSettings, attributes, specialEffects) -> new Biome(climateSettings, attributes, specialEffects, BiomeGenerationSettings.EMPTY)));
   public static final Codec<Holder<Biome>> CODEC;
   public static final Codec<HolderSet<Biome>> LIST_CODEC;
   private static final Noise TEMPERATURE_NOISE;
   @VisibleForTesting
   public static final Noise FROZEN_TEMPERATURE_NOISE;
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static final Noise BIOME_INFO_NOISE;
   private static final int TEMPERATURE_CACHE_SIZE = 1024;
   private final ClimateSettings climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final EnvironmentAttributeMap attributes;
   private final BiomeSpecialEffects specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
      Long2FloatLinkedOpenHashMap map = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
         {
            Objects.requireNonNull(Biome.this);
         }

         protected void rehash(final int newN) {
         }
      };
      map.defaultReturnValue(0.0F / 0.0F);
      return map;
   });

   private Biome(final ClimateSettings climateSettings, final EnvironmentAttributeMap attributes, final BiomeSpecialEffects specialEffects, final BiomeGenerationSettings generationSettings) {
      super();
      this.climateSettings = climateSettings;
      this.attributes = attributes;
      this.specialEffects = specialEffects;
      this.generationSettings = generationSettings;
   }

   public boolean hasPrecipitation() {
      return this.climateSettings.hasPrecipitation();
   }

   public Precipitation getPrecipitationAt(final BlockPos pos, final int seaLevel) {
      if (!this.hasPrecipitation()) {
         return Biome.Precipitation.NONE;
      } else {
         return this.coldEnoughToSnow(pos, seaLevel) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
      }
   }

   private float getHeightAdjustedTemperature(final BlockPos pos, final int seaLevel) {
      float adjustedTemperature = this.climateSettings.temperatureModifier.modifyTemperature(pos, this.getBaseTemperature());
      int snowLevel = seaLevel + 17;
      if (pos.getY() > snowLevel) {
         float v = TEMPERATURE_NOISE.get((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 8.0F;
         return adjustedTemperature - (v + (float)pos.getY() - (float)snowLevel) * 0.05F / 40.0F;
      } else {
         return adjustedTemperature;
      }
   }

   /** @deprecated */
   @Deprecated
   private float getTemperature(final BlockPos pos, final int seaLevel) {
      long key = pos.asLong();
      Long2FloatLinkedOpenHashMap cache = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
      float cached = cache.get(key);
      if (!Float.isNaN(cached)) {
         return cached;
      } else {
         float temp = this.getHeightAdjustedTemperature(pos, seaLevel);
         if (cache.size() == 1024) {
            cache.removeFirstFloat();
         }

         cache.put(key, temp);
         return temp;
      }
   }

   public boolean shouldFreeze(final LevelReader level, final BlockPos pos) {
      return this.shouldFreeze(level, pos, true);
   }

   public boolean shouldFreeze(final LevelReader level, final BlockPos pos, final boolean checkNeighbors) {
      if (this.warmEnoughToRain(pos, level.getSeaLevel())) {
         return false;
      } else {
         if (level.isInsideBuildHeight(pos.getY()) && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
            BlockState blockState = level.getBlockState(pos);
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.is(Fluids.WATER) && blockState.getBlock() instanceof LiquidBlock) {
               if (!checkNeighbors) {
                  return true;
               }

               boolean surroundedByWater = level.isWaterAt(pos.west()) && level.isWaterAt(pos.east()) && level.isWaterAt(pos.north()) && level.isWaterAt(pos.south());
               if (!surroundedByWater) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean coldEnoughToSnow(final BlockPos pos, final int seaLevel) {
      return !this.warmEnoughToRain(pos, seaLevel);
   }

   public boolean warmEnoughToRain(final BlockPos pos, final int seaLevel) {
      return this.getTemperature(pos, seaLevel) >= 0.15F;
   }

   public boolean shouldMeltFrozenOceanIcebergSlightly(final BlockPos pos, final int seaLevel) {
      return this.getTemperature(pos, seaLevel) > 0.1F;
   }

   public boolean shouldSnow(final LevelReader level, final BlockPos pos) {
      if (this.getPrecipitationAt(pos, level.getSeaLevel()) != Biome.Precipitation.SNOW) {
         return false;
      } else {
         if (level.isInsideBuildHeight(pos.getY()) && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
            BlockState state = level.getBlockState(pos);
            if ((state.isAir() || state.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive(level, pos)) {
               return true;
            }
         }

         return false;
      }
   }

   public BiomeGenerationSettings getGenerationSettings() {
      return this.generationSettings;
   }

   public int getGrassColor(final double x, final double z) {
      int baseGrassColor = this.getBaseGrassColor();
      return this.specialEffects.grassColorModifier().modifyColor(x, z, baseGrassColor);
   }

   private int getBaseGrassColor() {
      Optional<Integer> colorOverride = this.specialEffects.grassColorOverride();
      return colorOverride.isPresent() ? (Integer)colorOverride.get() : this.getGrassColorFromTexture();
   }

   private int getGrassColorFromTexture() {
      double temp = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double rain = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return GrassColor.get(temp, rain);
   }

   public int getFoliageColor() {
      return (Integer)this.specialEffects.foliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
   }

   private int getFoliageColorFromTexture() {
      double temp = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double rain = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return FoliageColor.get(temp, rain);
   }

   public int getDryFoliageColor() {
      return (Integer)this.specialEffects.dryFoliageColorOverride().orElseGet(this::getDryFoliageColorFromTexture);
   }

   private int getDryFoliageColorFromTexture() {
      double temp = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double rain = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return DryFoliageColor.get(temp, rain);
   }

   public float getBaseTemperature() {
      return this.climateSettings.temperature;
   }

   public EnvironmentAttributeMap getAttributes() {
      return this.attributes;
   }

   public BiomeSpecialEffects getSpecialEffects() {
      return this.specialEffects;
   }

   public int getWaterColor() {
      return this.specialEffects.waterColor();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.BIOME, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.holderSet(Registries.BIOME, DIRECT_CODEC);
      TEMPERATURE_NOISE = new SimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), true);
      FROZEN_TEMPERATURE_NOISE = (Noise)Util.make(() -> {
         WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(3456L));
         return NoiseStack.builder().add(new SimplexNoise(random, true), 1.0, 0.14285715F).add(new SimplexNoise(random, true), 0.5, 0.2857143F).add(new SimplexNoise(random, true), 0.25, 0.5714286F).build();
      });
      BIOME_INFO_NOISE = new SimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), true);
   }

   public static enum Precipitation implements StringRepresentable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec<Precipitation> CODEC = StringRepresentable.<Precipitation>fromEnum(Precipitation::values);
      private final String name;

      private Precipitation(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Precipitation[] $values() {
         return new Precipitation[]{NONE, RAIN, SNOW};
      }
   }

   public static enum TemperatureModifier implements StringRepresentable {
      NONE("none") {
         public float modifyTemperature(final BlockPos pos, final float baseTemperature) {
            return baseTemperature;
         }
      },
      FROZEN("frozen") {
         public float modifyTemperature(final BlockPos pos, final float baseTemperature) {
            double groundValueLargeVariation = (double)(Biome.FROZEN_TEMPERATURE_NOISE.get((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05) * 7.0F);
            double groundValueEdgeVariation = (double)Biome.BIOME_INFO_NOISE.get((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2);
            double icePatches = groundValueLargeVariation + groundValueEdgeVariation;
            if (icePatches < 0.3) {
               double groundValueSmallVariation = (double)Biome.BIOME_INFO_NOISE.get((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09);
               if (groundValueSmallVariation < 0.8) {
                  return 0.2F;
               }
            }

            return baseTemperature;
         }
      };

      private final String name;
      public static final Codec<TemperatureModifier> CODEC = StringRepresentable.<TemperatureModifier>fromEnum(TemperatureModifier::values);

      public abstract float modifyTemperature(final BlockPos pos, final float baseTemperature);

      private TemperatureModifier(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static TemperatureModifier[] $values() {
         return new TemperatureModifier[]{NONE, FROZEN};
      }
   }

   public static class BiomeBuilder {
      private boolean hasPrecipitation = true;
      private @Nullable Float temperature;
      private TemperatureModifier temperatureModifier;
      private @Nullable Float downfall;
      private final EnvironmentAttributeMap.Builder attributes;
      private @Nullable BiomeSpecialEffects specialEffects;
      private @Nullable BiomeGenerationSettings generationSettings;

      public BiomeBuilder() {
         super();
         this.temperatureModifier = Biome.TemperatureModifier.NONE;
         this.attributes = EnvironmentAttributeMap.builder();
      }

      public BiomeBuilder hasPrecipitation(final boolean hasPrecipitation) {
         this.hasPrecipitation = hasPrecipitation;
         return this;
      }

      public BiomeBuilder temperature(final float temperature) {
         this.temperature = temperature;
         return this;
      }

      public BiomeBuilder downfall(final float downfall) {
         this.downfall = downfall;
         return this;
      }

      public BiomeBuilder putAttributes(final EnvironmentAttributeMap attributes) {
         this.attributes.putAll(attributes);
         return this;
      }

      public BiomeBuilder putAttributes(final EnvironmentAttributeMap.Builder attributes) {
         return this.putAttributes(attributes.build());
      }

      public <Value> BiomeBuilder setAttribute(final EnvironmentAttribute<Value> attribute, final Value value) {
         this.attributes.set(attribute, value);
         return this;
      }

      public <Value, Parameter> BiomeBuilder modifyAttribute(final EnvironmentAttribute<Value> attribute, final AttributeModifier<Value, Parameter> modifier, final Parameter value) {
         this.attributes.modify(attribute, modifier, value);
         return this;
      }

      public BiomeBuilder specialEffects(final BiomeSpecialEffects specialEffects) {
         this.specialEffects = specialEffects;
         return this;
      }

      public BiomeBuilder mobSpawnSettings(final MobSpawnSettings mobSpawnSettings) {
         this.modifyAttribute(EnvironmentAttributes.NATURAL_MOB_SPAWNS, MobSpawnSettingsModifier.overlay(), mobSpawnSettings);
         return this;
      }

      public BiomeBuilder generationSettings(final BiomeGenerationSettings generationSettings) {
         this.generationSettings = generationSettings;
         return this;
      }

      public BiomeBuilder temperatureAdjustment(final TemperatureModifier temperatureModifier) {
         this.temperatureModifier = temperatureModifier;
         return this;
      }

      public Biome build() {
         if (this.temperature != null && this.downfall != null && this.specialEffects != null && this.generationSettings != null) {
            return new Biome(new ClimateSettings(this.hasPrecipitation, this.temperature, this.temperatureModifier, this.downfall), this.attributes.build(), this.specialEffects, this.generationSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + String.valueOf(this));
         }
      }

      public String toString() {
         boolean var10000 = this.hasPrecipitation;
         return "BiomeBuilder{\nhasPrecipitation=" + var10000 + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + String.valueOf(this.temperatureModifier) + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + String.valueOf(this.specialEffects) + ",\ngenerationSettings=" + String.valueOf(this.generationSettings) + ",\n}";
      }
   }

   private static record ClimateSettings(boolean hasPrecipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
      public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("has_precipitation").forGetter((b) -> b.hasPrecipitation), Codec.FLOAT.fieldOf("temperature").forGetter((b) -> b.temperature), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((b) -> b.temperatureModifier), Codec.FLOAT.fieldOf("downfall").forGetter((b) -> b.downfall)).apply(i, ClimateSettings::new));

      private ClimateSettings {
         super();
      }
   }
}
