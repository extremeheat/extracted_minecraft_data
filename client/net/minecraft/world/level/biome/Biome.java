package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
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
      }), Codec.FLOAT.fieldOf("depth").forGetter((var0x) -> {
         return var0x.depth;
      }), Codec.FLOAT.fieldOf("scale").forGetter((var0x) -> {
         return var0x.scale;
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
      }), Codec.FLOAT.fieldOf("depth").forGetter((var0x) -> {
         return var0x.depth;
      }), Codec.FLOAT.fieldOf("scale").forGetter((var0x) -> {
         return var0x.scale;
      }), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((var0x) -> {
         return var0x.specialEffects;
      })).apply(var0, (var0x, var1, var2, var3, var4) -> {
         return new Biome(var0x, var1, var2, var3, var4, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY);
      });
   });
   public static final Codec<Supplier<Biome>> CODEC;
   public static final Codec<List<Supplier<Biome>>> LIST_CODEC;
   private final Map<Integer, List<StructureFeature<?>>> structuresByStep;
   private static final PerlinSimplexNoise TEMPERATURE_NOISE;
   private static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE;
   public static final PerlinSimplexNoise BIOME_INFO_NOISE;
   private final Biome.ClimateSettings climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final MobSpawnSettings mobSettings;
   private final float depth;
   private final float scale;
   private final Biome.BiomeCategory biomeCategory;
   private final BiomeSpecialEffects specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache;

   private Biome(Biome.ClimateSettings var1, Biome.BiomeCategory var2, float var3, float var4, BiomeSpecialEffects var5, BiomeGenerationSettings var6, MobSpawnSettings var7) {
      super();
      this.structuresByStep = (Map)Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((var0) -> {
         return var0.step().ordinal();
      }));
      this.temperatureCache = ThreadLocal.withInitial(() -> {
         return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
            Long2FloatLinkedOpenHashMap var1 = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
               protected void rehash(int var1) {
               }
            };
            var1.defaultReturnValue(0.0F / 0.0);
            return var1;
         });
      });
      this.climateSettings = var1;
      this.generationSettings = var6;
      this.mobSettings = var7;
      this.biomeCategory = var2;
      this.depth = var3;
      this.scale = var4;
      this.specialEffects = var5;
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
      if (var1.getY() > 64) {
         float var3 = (float)(TEMPERATURE_NOISE.getValue((double)((float)var1.getX() / 8.0F), (double)((float)var1.getZ() / 8.0F), false) * 4.0D);
         return var2 - (var3 + (float)var1.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return var2;
      }
   }

   public final float getTemperature(BlockPos var1) {
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
      if (this.getTemperature(var2) >= 0.15F) {
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

   public boolean shouldSnow(LevelReader var1, BlockPos var2) {
      if (this.getTemperature(var2) >= 0.15F) {
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

   public void generate(StructureFeatureManager var1, ChunkGenerator var2, WorldGenRegion var3, long var4, WorldgenRandom var6, BlockPos var7) {
      List var8 = this.generationSettings.features();
      int var9 = GenerationStep.Decoration.values().length;

      for(int var10 = 0; var10 < var9; ++var10) {
         int var11 = 0;
         if (var1.shouldGenerateFeatures()) {
            List var12 = (List)this.structuresByStep.getOrDefault(var10, Collections.emptyList());

            for(Iterator var13 = var12.iterator(); var13.hasNext(); ++var11) {
               StructureFeature var14 = (StructureFeature)var13.next();
               var6.setFeatureSeed(var4, var11, var10);
               int var15 = SectionPos.blockToSectionCoord(var7.getX());
               int var16 = SectionPos.blockToSectionCoord(var7.getZ());
               int var17 = SectionPos.sectionToBlockCoord(var15);
               int var18 = SectionPos.sectionToBlockCoord(var16);

               try {
                  var1.startsForFeature(SectionPos.of(var7), var14).forEach((var8x) -> {
                     var8x.placeInChunk(var3, var1, var2, var6, new BoundingBox(var17, var3.getMinBuildHeight() + 1, var18, var17 + 15, var3.getMaxBuildHeight(), var18 + 15), new ChunkPos(var15, var16));
                  });
               } catch (Exception var21) {
                  CrashReport var20 = CrashReport.forThrowable(var21, "Feature placement");
                  var20.addCategory("Feature").setDetail("Id", (Object)Registry.STRUCTURE_FEATURE.getKey(var14)).setDetail("Description", () -> {
                     return var14.toString();
                  });
                  throw new ReportedException(var20);
               }
            }
         }

         if (var8.size() > var10) {
            for(Iterator var23 = ((List)var8.get(var10)).iterator(); var23.hasNext(); ++var11) {
               Supplier var24 = (Supplier)var23.next();
               ConfiguredFeature var25 = (ConfiguredFeature)var24.get();
               var6.setFeatureSeed(var4, var11, var10);

               try {
                  var25.place(var3, var2, var6, var7);
               } catch (Exception var22) {
                  CrashReport var26 = CrashReport.forThrowable(var22, "Feature placement");
                  var26.addCategory("Feature").setDetail("Id", (Object)Registry.FEATURE.getKey(var25.feature)).setDetail("Config", (Object)var25.config).setDetail("Description", () -> {
                     return var25.feature.toString();
                  });
                  throw new ReportedException(var26);
               }
            }
         }
      }

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

   public void buildSurfaceAt(Random var1, ChunkAccess var2, int var3, int var4, int var5, double var6, BlockState var8, BlockState var9, int var10, long var11) {
      ConfiguredSurfaceBuilder var13 = (ConfiguredSurfaceBuilder)this.generationSettings.getSurfaceBuilder().get();
      var13.initNoise(var11);
      var13.apply(var1, var2, this, var3, var4, var5, var6, var8, var9, var10, var11);
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.climateSettings.downfall;
   }

   public final float getScale() {
      return this.scale;
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

   // $FF: synthetic method
   Biome(Biome.ClimateSettings var1, Biome.BiomeCategory var2, float var3, float var4, BiomeSpecialEffects var5, BiomeGenerationSettings var6, MobSpawnSettings var7, Object var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.BIOME_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC);
      TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(1234L), ImmutableList.of(0));
      FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(3456L), ImmutableList.of(-2, -1, 0));
      BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(2345L), ImmutableList.of(0));
   }

   static class ClimateSettings {
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
      private final Biome.Precipitation precipitation;
      private final float temperature;
      private final Biome.TemperatureModifier temperatureModifier;
      private final float downfall;

      private ClimateSettings(Biome.Precipitation var1, float var2, Biome.TemperatureModifier var3, float var4) {
         super();
         this.precipitation = var1;
         this.temperature = var2;
         this.temperatureModifier = var3;
         this.downfall = var4;
      }

      // $FF: synthetic method
      ClimateSettings(Biome.Precipitation var1, float var2, Biome.TemperatureModifier var3, float var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   public static class ClimateParameters {
      public static final Codec<Biome.ClimateParameters> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.floatRange(-2.0F, 2.0F).fieldOf("temperature").forGetter((var0x) -> {
            return var0x.temperature;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("humidity").forGetter((var0x) -> {
            return var0x.humidity;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("altitude").forGetter((var0x) -> {
            return var0x.altitude;
         }), Codec.floatRange(-2.0F, 2.0F).fieldOf("weirdness").forGetter((var0x) -> {
            return var0x.weirdness;
         }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter((var0x) -> {
            return var0x.offset;
         })).apply(var0, Biome.ClimateParameters::new);
      });
      private final float temperature;
      private final float humidity;
      private final float altitude;
      private final float weirdness;
      private final float offset;

      public ClimateParameters(float var1, float var2, float var3, float var4, float var5) {
         super();
         this.temperature = var1;
         this.humidity = var2;
         this.altitude = var3;
         this.weirdness = var4;
         this.offset = var5;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Biome.ClimateParameters var2 = (Biome.ClimateParameters)var1;
            if (Float.compare(var2.temperature, this.temperature) != 0) {
               return false;
            } else if (Float.compare(var2.humidity, this.humidity) != 0) {
               return false;
            } else if (Float.compare(var2.altitude, this.altitude) != 0) {
               return false;
            } else {
               return Float.compare(var2.weirdness, this.weirdness) == 0;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.temperature != 0.0F ? Float.floatToIntBits(this.temperature) : 0;
         var1 = 31 * var1 + (this.humidity != 0.0F ? Float.floatToIntBits(this.humidity) : 0);
         var1 = 31 * var1 + (this.altitude != 0.0F ? Float.floatToIntBits(this.altitude) : 0);
         var1 = 31 * var1 + (this.weirdness != 0.0F ? Float.floatToIntBits(this.weirdness) : 0);
         return var1;
      }

      public float fitness(Biome.ClimateParameters var1) {
         return (this.temperature - var1.temperature) * (this.temperature - var1.temperature) + (this.humidity - var1.humidity) * (this.humidity - var1.humidity) + (this.altitude - var1.altitude) * (this.altitude - var1.altitude) + (this.weirdness - var1.weirdness) * (this.weirdness - var1.weirdness) + (this.offset - var1.offset) * (this.offset - var1.offset);
      }
   }

   public static class BiomeBuilder {
      @Nullable
      private Biome.Precipitation precipitation;
      @Nullable
      private Biome.BiomeCategory biomeCategory;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
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

      public Biome.BiomeBuilder depth(float var1) {
         this.depth = var1;
         return this;
      }

      public Biome.BiomeBuilder scale(float var1) {
         this.scale = var1;
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
         if (this.precipitation != null && this.biomeCategory != null && this.depth != null && this.scale != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
            return new Biome(new Biome.ClimateSettings(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.depth, this.scale, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
         }
      }

      public String toString() {
         return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n" + '}';
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

      private TemperatureModifier(String var3) {
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
      TemperatureModifier(String var3, Object var4) {
         this(var3);
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
      NETHER("nether");

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
   }
}
