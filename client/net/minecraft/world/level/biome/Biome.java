package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Biome {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Biome.ClimateSettings.CODEC.forGetter((var0x) -> {
         return var0x.climateSettings;
      }), Biome.BiomeCategory.CODEC.fieldOf("category").forGetter((var0x) -> {
         return var0x.biomeCategory;
      }), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((var0x) -> {
         return var0x.specialEffects;
      }), BiomeGenerationSettings.CODEC.forGetter((var0x) -> {
         return var0x.generationSettings;
      }), MobSpawnSettings.CODEC.forGetter((var0x) -> {
         return var0x.mobSettings;
      })).apply(var0, Biome::new);
   });
   public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Biome.ClimateSettings.CODEC.forGetter((var0x) -> {
         return var0x.climateSettings;
      }), Biome.BiomeCategory.CODEC.fieldOf("category").forGetter((var0x) -> {
         return var0x.biomeCategory;
      }), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((var0x) -> {
         return var0x.specialEffects;
      })).apply(var0, (var0x, var1, var2) -> {
         return new Biome(var0x, var1, var2, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY);
      });
   });
   public static final Codec<Supplier<Biome>> CODEC;
   public static final Codec<List<Supplier<Biome>>> LIST_CODEC;
   private static final PerlinSimplexNoise TEMPERATURE_NOISE;
   static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE;
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static final PerlinSimplexNoise BIOME_INFO_NOISE;
   private static final int TEMPERATURE_CACHE_SIZE = 1024;
   private final Biome.ClimateSettings climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final MobSpawnSettings mobSettings;
   private final Biome.BiomeCategory biomeCategory;
   private final BiomeSpecialEffects specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
      return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap var1 = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int var1) {
            }
         };
         var1.defaultReturnValue(0.0F / 0.0);
         return var1;
      });
   });

   Biome(Biome.ClimateSettings var1, Biome.BiomeCategory var2, BiomeSpecialEffects var3, BiomeGenerationSettings var4, MobSpawnSettings var5) {
      super();
      this.climateSettings = var1;
      this.generationSettings = var4;
      this.mobSettings = var5;
      this.biomeCategory = var2;
      this.specialEffects = var3;
   }

   public int getSkyColor() {
      return this.specialEffects.getSkyColor();
   }

   public MobSpawnSettings getMobSettings() {
      return this.mobSettings;
   }

   public Biome.Precipitation getPrecipitation() {
      return this.climateSettings.precipitation;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   private float getHeightAdjustedTemperature(BlockPos var1) {
      float var2 = this.climateSettings.temperatureModifier.modifyTemperature(var1, this.getBaseTemperature());
      if (var1.getY() > 80) {
         float var3 = (float)(TEMPERATURE_NOISE.getValue((double)((float)var1.getX() / 8.0F), (double)((float)var1.getZ() / 8.0F), false) * 8.0D);
         return var2 - (var3 + (float)var1.getY() - 80.0F) * 0.05F / 40.0F;
      } else {
         return var2;
      }
   }

   /** @deprecated */
   @Deprecated
   private float getTemperature(BlockPos var1) {
      long var2 = var1.asLong();
      Long2FloatLinkedOpenHashMap var4 = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
      float var5 = var4.get(var2);
      if (!Float.isNaN(var5)) {
         return var5;
      } else {
         float var6 = this.getHeightAdjustedTemperature(var1);
         if (var4.size() == 1024) {
            var4.removeFirstFloat();
         }

         var4.put(var2, var6);
         return var6;
      }
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2) {
      return this.shouldFreeze(var1, var2, true);
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2, boolean var3) {
      if (this.warmEnoughToRain(var2)) {
         return false;
      } else {
         if (var2.getY() >= var1.getMinBuildHeight() && var2.getY() < var1.getMaxBuildHeight() && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
            BlockState var4 = var1.getBlockState(var2);
            FluidState var5 = var1.getFluidState(var2);
            if (var5.getType() == Fluids.WATER && var4.getBlock() instanceof LiquidBlock) {
               if (!var3) {
                  return true;
               }

               boolean var6 = var1.isWaterAt(var2.west()) && var1.isWaterAt(var2.east()) && var1.isWaterAt(var2.north()) && var1.isWaterAt(var2.south());
               if (!var6) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean coldEnoughToSnow(BlockPos var1) {
      return !this.warmEnoughToRain(var1);
   }

   public boolean warmEnoughToRain(BlockPos var1) {
      return this.getTemperature(var1) >= 0.15F;
   }

   public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPos var1) {
      return this.getTemperature(var1) > 0.1F;
   }

   public boolean shouldSnowGolemBurn(BlockPos var1) {
      return this.getTemperature(var1) > 1.0F;
   }

   public boolean shouldSnow(LevelReader var1, BlockPos var2) {
      if (this.warmEnoughToRain(var2)) {
         return false;
      } else {
         if (var2.getY() >= var1.getMinBuildHeight() && var2.getY() < var1.getMaxBuildHeight() && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
            BlockState var3 = var1.getBlockState(var2);
            if (var3.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(var1, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public BiomeGenerationSettings getGenerationSettings() {
      return this.generationSettings;
   }

   public int getFogColor() {
      return this.specialEffects.getFogColor();
   }

   public int getGrassColor(double var1, double var3) {
      int var5 = (Integer)this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
      return this.specialEffects.getGrassColorModifier().modifyColor(var1, var3, var5);
   }

   private int getGrassColorFromTexture() {
      double var1 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double var3 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return GrassColor.get(var1, var3);
   }

   public int getFoliageColor() {
      return (Integer)this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
   }

   private int getFoliageColorFromTexture() {
      double var1 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double var3 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return FoliageColor.get(var1, var3);
   }

   public final float getDownfall() {
      return this.climateSettings.downfall;
   }

   public final float getBaseTemperature() {
      return this.climateSettings.temperature;
   }

   public BiomeSpecialEffects getSpecialEffects() {
      return this.specialEffects;
   }

   public final int getWaterColor() {
      return this.specialEffects.getWaterColor();
   }

   public final int getWaterFogColor() {
      return this.specialEffects.getWaterFogColor();
   }

   public Optional<AmbientParticleSettings> getAmbientParticle() {
      return this.specialEffects.getAmbientParticleSettings();
   }

   public Optional<SoundEvent> getAmbientLoop() {
      return this.specialEffects.getAmbientLoopSoundEvent();
   }

   public Optional<AmbientMoodSettings> getAmbientMood() {
      return this.specialEffects.getAmbientMoodSettings();
   }

   public Optional<AmbientAdditionsSettings> getAmbientAdditions() {
      return this.specialEffects.getAmbientAdditionsSettings();
   }

   public Optional<Music> getBackgroundMusic() {
      return this.specialEffects.getBackgroundMusic();
   }

   public final Biome.BiomeCategory getBiomeCategory() {
      return this.biomeCategory;
   }

   public String toString() {
      ResourceLocation var1 = BuiltinRegistries.BIOME.getKey(this);
      return var1 == null ? super.toString() : var1.toString();
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.BIOME_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC);
      TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
      FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0));
      BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
   }

   private static class ClimateSettings {
      public static final MapCodec<Biome.ClimateSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter((var0x) -> {
            return var0x.precipitation;
         }), Codec.FLOAT.fieldOf("temperature").forGetter((var0x) -> {
            return var0x.temperature;
         }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((var0x) -> {
            return var0x.temperatureModifier;
         }), Codec.FLOAT.fieldOf("downfall").forGetter((var0x) -> {
            return var0x.downfall;
         })).apply(var0, Biome.ClimateSettings::new);
      });
      final Biome.Precipitation precipitation;
      final float temperature;
      final Biome.TemperatureModifier temperatureModifier;
      final float downfall;

      ClimateSettings(Biome.Precipitation var1, float var2, Biome.TemperatureModifier var3, float var4) {
         super();
         this.precipitation = var1;
         this.temperature = var2;
         this.temperatureModifier = var3;
         this.downfall = var4;
      }
   }

   public static enum BiomeCategory implements StringRepresentable {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether"),
      UNDERGROUND("underground"),
      MOUNTAIN("mountain");

      public static final Codec<Biome.BiomeCategory> CODEC = StringRepresentable.fromEnum(Biome.BiomeCategory::values, Biome.BiomeCategory::byName);
      private static final Map<String, Biome.BiomeCategory> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeCategory::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private BiomeCategory(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.BiomeCategory byName(String var0) {
         return (Biome.BiomeCategory)BY_NAME.get(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Biome.BiomeCategory[] $values() {
         return new Biome.BiomeCategory[]{NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS, SAVANNA, ICY, THEEND, BEACH, FOREST, OCEAN, DESERT, RIVER, SWAMP, MUSHROOM, NETHER, UNDERGROUND, MOUNTAIN};
      }
   }

   public static enum Precipitation implements StringRepresentable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec<Biome.Precipitation> CODEC = StringRepresentable.fromEnum(Biome.Precipitation::values, Biome.Precipitation::byName);
      private static final Map<String, Biome.Precipitation> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.Precipitation::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Precipitation(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.Precipitation byName(String var0) {
         return (Biome.Precipitation)BY_NAME.get(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Biome.Precipitation[] $values() {
         return new Biome.Precipitation[]{NONE, RAIN, SNOW};
      }
   }

   public static enum TemperatureModifier implements StringRepresentable {
      NONE("none") {
         public float modifyTemperature(BlockPos var1, float var2) {
            return var2;
         }
      },
      FROZEN("frozen") {
         public float modifyTemperature(BlockPos var1, float var2) {
            double var3 = Biome.FROZEN_TEMPERATURE_NOISE.getValue((double)var1.getX() * 0.05D, (double)var1.getZ() * 0.05D, false) * 7.0D;
            double var5 = Biome.BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.2D, (double)var1.getZ() * 0.2D, false);
            double var7 = var3 + var5;
            if (var7 < 0.3D) {
               double var9 = Biome.BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.09D, (double)var1.getZ() * 0.09D, false);
               if (var9 < 0.8D) {
                  return 0.2F;
               }
            }

            return var2;
         }
      };

      private final String name;
      public static final Codec<Biome.TemperatureModifier> CODEC = StringRepresentable.fromEnum(Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName);
      private static final Map<String, Biome.TemperatureModifier> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.TemperatureModifier::getName, (var0) -> {
         return var0;
      }));

      public abstract float modifyTemperature(BlockPos var1, float var2);

      TemperatureModifier(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Biome.TemperatureModifier byName(String var0) {
         return (Biome.TemperatureModifier)BY_NAME.get(var0);
      }

      // $FF: synthetic method
      private static Biome.TemperatureModifier[] $values() {
         return new Biome.TemperatureModifier[]{NONE, FROZEN};
      }
   }

   public static class BiomeBuilder {
      @Nullable
      private Biome.Precipitation precipitation;
      @Nullable
      private Biome.BiomeCategory biomeCategory;
      @Nullable
      private Float temperature;
      private Biome.TemperatureModifier temperatureModifier;
      @Nullable
      private Float downfall;
      @Nullable
      private BiomeSpecialEffects specialEffects;
      @Nullable
      private MobSpawnSettings mobSpawnSettings;
      @Nullable
      private BiomeGenerationSettings generationSettings;

      public BiomeBuilder() {
         super();
         this.temperatureModifier = Biome.TemperatureModifier.NONE;
      }

      public Biome.BiomeBuilder precipitation(Biome.Precipitation var1) {
         this.precipitation = var1;
         return this;
      }

      public Biome.BiomeBuilder biomeCategory(Biome.BiomeCategory var1) {
         this.biomeCategory = var1;
         return this;
      }

      public Biome.BiomeBuilder temperature(float var1) {
         this.temperature = var1;
         return this;
      }

      public Biome.BiomeBuilder downfall(float var1) {
         this.downfall = var1;
         return this;
      }

      public Biome.BiomeBuilder specialEffects(BiomeSpecialEffects var1) {
         this.specialEffects = var1;
         return this;
      }

      public Biome.BiomeBuilder mobSpawnSettings(MobSpawnSettings var1) {
         this.mobSpawnSettings = var1;
         return this;
      }

      public Biome.BiomeBuilder generationSettings(BiomeGenerationSettings var1) {
         this.generationSettings = var1;
         return this;
      }

      public Biome.BiomeBuilder temperatureAdjustment(Biome.TemperatureModifier var1) {
         this.temperatureModifier = var1;
         return this;
      }

      public Biome build() {
         if (this.precipitation != null && this.biomeCategory != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
            return new Biome(new Biome.ClimateSettings(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
         }
      }

      public String toString() {
         return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
      }
   }
}
