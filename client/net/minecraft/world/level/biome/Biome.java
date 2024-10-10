package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
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

public final class Biome {
   public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Biome.ClimateSettings.CODEC.forGetter(var0x -> var0x.climateSettings),
               BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter(var0x -> var0x.specialEffects),
               BiomeGenerationSettings.CODEC.forGetter(var0x -> var0x.generationSettings),
               MobSpawnSettings.CODEC.forGetter(var0x -> var0x.mobSettings)
            )
            .apply(var0, Biome::new)
   );
   public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Biome.ClimateSettings.CODEC.forGetter(var0x -> var0x.climateSettings),
               BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter(var0x -> var0x.specialEffects)
            )
            .apply(var0, (var0x, var1) -> new Biome(var0x, var1, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY))
   );
   public static final Codec<Holder<Biome>> CODEC = RegistryFileCodec.create(Registries.BIOME, DIRECT_CODEC);
   public static final Codec<HolderSet<Biome>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.BIOME, DIRECT_CODEC);
   private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
   static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(
      new WorldgenRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0)
   );
   @Deprecated(
      forRemoval = true
   )
   public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
   private static final int TEMPERATURE_CACHE_SIZE = 1024;
   private final Biome.ClimateSettings climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final MobSpawnSettings mobSettings;
   private final BiomeSpecialEffects specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
         Long2FloatLinkedOpenHashMap var1x = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int var1) {
            }
         };
         var1x.defaultReturnValue(0.0F / 0.0F);
         return var1x;
      }));

   Biome(Biome.ClimateSettings var1, BiomeSpecialEffects var2, BiomeGenerationSettings var3, MobSpawnSettings var4) {
      super();
      this.climateSettings = var1;
      this.generationSettings = var3;
      this.mobSettings = var4;
      this.specialEffects = var2;
   }

   public int getSkyColor() {
      return this.specialEffects.getSkyColor();
   }

   public MobSpawnSettings getMobSettings() {
      return this.mobSettings;
   }

   public boolean hasPrecipitation() {
      return this.climateSettings.hasPrecipitation();
   }

   public Biome.Precipitation getPrecipitationAt(BlockPos var1, int var2) {
      if (!this.hasPrecipitation()) {
         return Biome.Precipitation.NONE;
      } else {
         return this.coldEnoughToSnow(var1, var2) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
      }
   }

   private float getHeightAdjustedTemperature(BlockPos var1, int var2) {
      float var3 = this.climateSettings.temperatureModifier.modifyTemperature(var1, this.getBaseTemperature());
      int var4 = var2 + 17;
      if (var1.getY() > var4) {
         float var5 = (float)(TEMPERATURE_NOISE.getValue((double)((float)var1.getX() / 8.0F), (double)((float)var1.getZ() / 8.0F), false) * 8.0);
         return var3 - (var5 + (float)var1.getY() - (float)var4) * 0.05F / 40.0F;
      } else {
         return var3;
      }
   }

   @Deprecated
   private float getTemperature(BlockPos var1, int var2) {
      long var3 = var1.asLong();
      Long2FloatLinkedOpenHashMap var5 = this.temperatureCache.get();
      float var6 = var5.get(var3);
      if (!Float.isNaN(var6)) {
         return var6;
      } else {
         float var7 = this.getHeightAdjustedTemperature(var1, var2);
         if (var5.size() == 1024) {
            var5.removeFirstFloat();
         }

         var5.put(var3, var7);
         return var7;
      }
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2) {
      return this.shouldFreeze(var1, var2, true);
   }

   public boolean shouldFreeze(LevelReader var1, BlockPos var2, boolean var3) {
      if (this.warmEnoughToRain(var2, var1.getSeaLevel())) {
         return false;
      } else {
         if (var1.isInsideBuildHeight(var2.getY()) && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
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

   public boolean coldEnoughToSnow(BlockPos var1, int var2) {
      return !this.warmEnoughToRain(var1, var2);
   }

   public boolean warmEnoughToRain(BlockPos var1, int var2) {
      return this.getTemperature(var1, var2) >= 0.15F;
   }

   public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPos var1, int var2) {
      return this.getTemperature(var1, var2) > 0.1F;
   }

   public boolean shouldSnow(LevelReader var1, BlockPos var2) {
      if (this.warmEnoughToRain(var2, var1.getSeaLevel())) {
         return false;
      } else {
         if (var1.isInsideBuildHeight(var2.getY()) && var1.getBrightness(LightLayer.BLOCK, var2) < 10) {
            BlockState var3 = var1.getBlockState(var2);
            if ((var3.isAir() || var3.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive(var1, var2)) {
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
      int var5 = this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
      return this.specialEffects.getGrassColorModifier().modifyColor(var1, var3, var5);
   }

   private int getGrassColorFromTexture() {
      double var1 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double var3 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return GrassColor.get(var1, var3);
   }

   public int getFoliageColor() {
      return this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
   }

   private int getFoliageColorFromTexture() {
      double var1 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double var3 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return FoliageColor.get(var1, var3);
   }

   public float getBaseTemperature() {
      return this.climateSettings.temperature;
   }

   public BiomeSpecialEffects getSpecialEffects() {
      return this.specialEffects;
   }

   public int getWaterColor() {
      return this.specialEffects.getWaterColor();
   }

   public int getWaterFogColor() {
      return this.specialEffects.getWaterFogColor();
   }

   public Optional<AmbientParticleSettings> getAmbientParticle() {
      return this.specialEffects.getAmbientParticleSettings();
   }

   public Optional<Holder<SoundEvent>> getAmbientLoop() {
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

   public static class BiomeBuilder {
      private boolean hasPrecipitation = true;
      @Nullable
      private Float temperature;
      private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
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
      }

      public Biome.BiomeBuilder hasPrecipitation(boolean var1) {
         this.hasPrecipitation = var1;
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
         if (this.temperature != null
            && this.downfall != null
            && this.specialEffects != null
            && this.mobSpawnSettings != null
            && this.generationSettings != null) {
            return new Biome(
               new Biome.ClimateSettings(this.hasPrecipitation, this.temperature, this.temperatureModifier, this.downfall),
               this.specialEffects,
               this.generationSettings,
               this.mobSpawnSettings
            );
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
         }
      }

      @Override
      public String toString() {
         return "BiomeBuilder{\nhasPrecipitation="
            + this.hasPrecipitation
            + ",\ntemperature="
            + this.temperature
            + ",\ntemperatureModifier="
            + this.temperatureModifier
            + ",\ndownfall="
            + this.downfall
            + ",\nspecialEffects="
            + this.specialEffects
            + ",\nmobSpawnSettings="
            + this.mobSpawnSettings
            + ",\ngenerationSettings="
            + this.generationSettings
            + ",\n}";
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static enum Precipitation implements StringRepresentable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec<Biome.Precipitation> CODEC = StringRepresentable.fromEnum(Biome.Precipitation::values);
      private final String name;

      private Precipitation(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum TemperatureModifier implements StringRepresentable {
      NONE("none") {
         @Override
         public float modifyTemperature(BlockPos var1, float var2) {
            return var2;
         }
      },
      FROZEN("frozen") {
         @Override
         public float modifyTemperature(BlockPos var1, float var2) {
            double var3 = Biome.FROZEN_TEMPERATURE_NOISE.getValue((double)var1.getX() * 0.05, (double)var1.getZ() * 0.05, false) * 7.0;
            double var5 = Biome.BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.2, (double)var1.getZ() * 0.2, false);
            double var7 = var3 + var5;
            if (var7 < 0.3) {
               double var9 = Biome.BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.09, (double)var1.getZ() * 0.09, false);
               if (var9 < 0.8) {
                  return 0.2F;
               }
            }

            return var2;
         }
      };

      private final String name;
      public static final Codec<Biome.TemperatureModifier> CODEC = StringRepresentable.fromEnum(Biome.TemperatureModifier::values);

      public abstract float modifyTemperature(BlockPos var1, float var2);

      TemperatureModifier(final String nullxx) {
         this.name = nullxx;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
