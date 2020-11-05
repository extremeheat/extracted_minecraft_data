package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeGenerationSettings {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(() -> {
      return SurfaceBuilders.NOPE;
   }, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      RecordCodecBuilder var10001 = ConfiguredSurfaceBuilder.CODEC.fieldOf("surface_builder").forGetter((var0x) -> {
         return var0x.surfaceBuilder;
      });
      Codec var10002 = GenerationStep.Carving.CODEC;
      Codec var10003 = ConfiguredWorldCarver.LIST_CODEC;
      Logger var10005 = LOGGER;
      var10005.getClass();
      RecordCodecBuilder var1 = Codec.simpleMap(var10002, var10003.promotePartial(Util.prefix("Carver: ", var10005::error)), StringRepresentable.keys(GenerationStep.Carving.values())).fieldOf("carvers").forGetter((var0x) -> {
         return var0x.carvers;
      });
      var10003 = ConfiguredFeature.LIST_CODEC;
      var10005 = LOGGER;
      var10005.getClass();
      RecordCodecBuilder var2 = var10003.promotePartial(Util.prefix("Feature: ", var10005::error)).listOf().fieldOf("features").forGetter((var0x) -> {
         return var0x.features;
      });
      Codec var10004 = ConfiguredStructureFeature.LIST_CODEC;
      Logger var10006 = LOGGER;
      var10006.getClass();
      return var0.group(var10001, var1, var2, var10004.promotePartial(Util.prefix("Structure start: ", var10006::error)).fieldOf("starts").forGetter((var0x) -> {
         return var0x.structureStarts;
      })).apply(var0, BiomeGenerationSettings::new);
   });
   private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
   private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers;
   private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
   private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureStarts;
   private final List<ConfiguredFeature<?, ?>> flowerFeatures;

   private BiomeGenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> var1, Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> var2, List<List<Supplier<ConfiguredFeature<?, ?>>>> var3, List<Supplier<ConfiguredStructureFeature<?, ?>>> var4) {
      super();
      this.surfaceBuilder = var1;
      this.carvers = var2;
      this.features = var3;
      this.structureStarts = var4;
      this.flowerFeatures = (List)var3.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(ConfiguredFeature::getFeatures).filter((var0) -> {
         return var0.feature == Feature.FLOWER;
      }).collect(ImmutableList.toImmutableList());
   }

   public List<Supplier<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving var1) {
      return (List)this.carvers.getOrDefault(var1, ImmutableList.of());
   }

   public boolean isValidStart(StructureFeature<?> var1) {
      return this.structureStarts.stream().anyMatch((var1x) -> {
         return ((ConfiguredStructureFeature)var1x.get()).feature == var1;
      });
   }

   public Collection<Supplier<ConfiguredStructureFeature<?, ?>>> structures() {
      return this.structureStarts;
   }

   public ConfiguredStructureFeature<?, ?> withBiomeConfig(ConfiguredStructureFeature<?, ?> var1) {
      return (ConfiguredStructureFeature)DataFixUtils.orElse(this.structureStarts.stream().map(Supplier::get).filter((var1x) -> {
         return var1x.feature == var1.feature;
      }).findAny(), var1);
   }

   public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
      return this.flowerFeatures;
   }

   public List<List<Supplier<ConfiguredFeature<?, ?>>>> features() {
      return this.features;
   }

   public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public SurfaceBuilderConfiguration getSurfaceBuilderConfig() {
      return ((ConfiguredSurfaceBuilder)this.surfaceBuilder.get()).config();
   }

   // $FF: synthetic method
   BiomeGenerationSettings(Supplier var1, Map var2, List var3, List var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Builder {
      private Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
      private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
      private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.newArrayList();
      private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureStarts = Lists.newArrayList();

      public Builder() {
         super();
      }

      public BiomeGenerationSettings.Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> var1) {
         return this.surfaceBuilder(() -> {
            return var1;
         });
      }

      public BiomeGenerationSettings.Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> var1) {
         this.surfaceBuilder = Optional.of(var1);
         return this;
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration var1, ConfiguredFeature<?, ?> var2) {
         return this.addFeature(var1.ordinal(), () -> {
            return var2;
         });
      }

      public BiomeGenerationSettings.Builder addFeature(int var1, Supplier<ConfiguredFeature<?, ?>> var2) {
         this.addFeatureStepsUpTo(var1);
         ((List)this.features.get(var1)).add(var2);
         return this;
      }

      public <C extends CarverConfiguration> BiomeGenerationSettings.Builder addCarver(GenerationStep.Carving var1, ConfiguredWorldCarver<C> var2) {
         ((List)this.carvers.computeIfAbsent(var1, (var0) -> {
            return Lists.newArrayList();
         })).add(() -> {
            return var2;
         });
         return this;
      }

      public BiomeGenerationSettings.Builder addStructureStart(ConfiguredStructureFeature<?, ?> var1) {
         this.structureStarts.add(() -> {
            return var1;
         });
         return this;
      }

      private void addFeatureStepsUpTo(int var1) {
         while(this.features.size() <= var1) {
            this.features.add(Lists.newArrayList());
         }

      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings((Supplier)this.surfaceBuilder.orElseThrow(() -> {
            return new IllegalStateException("Missing surface builder");
         }), (Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
            return ImmutableList.copyOf((Collection)var0.getValue());
         })), (List)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), ImmutableList.copyOf(this.structureStarts));
      }
   }
}
