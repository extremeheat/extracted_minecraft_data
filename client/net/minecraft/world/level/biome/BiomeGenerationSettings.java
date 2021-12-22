package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeGenerationSettings {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(ImmutableMap.of(), ImmutableList.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      Codec var10001 = GenerationStep.Carving.CODEC;
      Codec var10002 = ConfiguredWorldCarver.LIST_CODEC;
      Logger var10004 = LOGGER;
      Objects.requireNonNull(var10004);
      RecordCodecBuilder var1 = Codec.simpleMap(var10001, var10002.promotePartial(Util.prefix("Carver: ", var10004::error)).flatXmap(ExtraCodecs.nonNullSupplierListCheck(), ExtraCodecs.nonNullSupplierListCheck()), StringRepresentable.keys(GenerationStep.Carving.values())).fieldOf("carvers").forGetter((var0x) -> {
         return var0x.carvers;
      });
      var10002 = PlacedFeature.LIST_CODEC;
      var10004 = LOGGER;
      Objects.requireNonNull(var10004);
      return var0.group(var1, var10002.promotePartial(Util.prefix("Feature: ", var10004::error)).flatXmap(ExtraCodecs.nonNullSupplierListCheck(), ExtraCodecs.nonNullSupplierListCheck()).listOf().fieldOf("features").forGetter((var0x) -> {
         return var0x.features;
      })).apply(var0, BiomeGenerationSettings::new);
   });
   private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers;
   private final List<List<Supplier<PlacedFeature>>> features;
   private final List<ConfiguredFeature<?, ?>> flowerFeatures;
   private final Set<PlacedFeature> featureSet;

   BiomeGenerationSettings(Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> var1, List<List<Supplier<PlacedFeature>>> var2) {
      super();
      this.carvers = var1;
      this.features = var2;
      this.flowerFeatures = (List)var2.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(PlacedFeature::getFeatures).filter((var0) -> {
         return var0.feature == Feature.FLOWER;
      }).collect(ImmutableList.toImmutableList());
      this.featureSet = (Set)var2.stream().flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toSet());
   }

   public List<Supplier<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving var1) {
      return (List)this.carvers.getOrDefault(var1, ImmutableList.of());
   }

   public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
      return this.flowerFeatures;
   }

   public List<List<Supplier<PlacedFeature>>> features() {
      return this.features;
   }

   public boolean hasFeature(PlacedFeature var1) {
      return this.featureSet.contains(var1);
   }

   public static class Builder {
      private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
      private final List<List<Supplier<PlacedFeature>>> features = Lists.newArrayList();

      public Builder() {
         super();
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration var1, PlacedFeature var2) {
         return this.addFeature(var1.ordinal(), () -> {
            return var2;
         });
      }

      public BiomeGenerationSettings.Builder addFeature(int var1, Supplier<PlacedFeature> var2) {
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

      private void addFeatureStepsUpTo(int var1) {
         while(this.features.size() <= var1) {
            this.features.add(Lists.newArrayList());
         }

      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings((Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
            return ImmutableList.copyOf((Collection)var0.getValue());
         })), (List)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()));
      }
   }
}
