package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public class BiomeGenerationSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(HolderSet.direct(), List.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", LOGGER::error)).fieldOf("carvers").forGetter(var0x -> var0x.carvers),
               PlacedFeature.LIST_OF_LISTS_CODEC
                  .promotePartial(Util.prefix("Features: ", LOGGER::error))
                  .fieldOf("features")
                  .forGetter(var0x -> var0x.features)
            )
            .apply(var0, BiomeGenerationSettings::new)
   );
   private final HolderSet<ConfiguredWorldCarver<?>> carvers;
   private final List<HolderSet<PlacedFeature>> features;
   private final Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures;
   private final Supplier<Set<PlacedFeature>> featureSet;

   BiomeGenerationSettings(HolderSet<ConfiguredWorldCarver<?>> var1, List<HolderSet<PlacedFeature>> var2) {
      super();
      this.carvers = var1;
      this.features = var2;
      this.flowerFeatures = Suppliers.memoize(
         () -> var2.stream()
               .flatMap(HolderSet::stream)
               .map(Holder::value)
               .flatMap(PlacedFeature::getFeatures)
               .filter(var0x -> var0x.feature() == Feature.FLOWER)
               .collect(ImmutableList.toImmutableList())
      );
      this.featureSet = Suppliers.memoize(() -> var2.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet()));
   }

   public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers() {
      return this.carvers;
   }

   public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
      return this.flowerFeatures.get();
   }

   public List<HolderSet<PlacedFeature>> features() {
      return this.features;
   }

   public boolean hasFeature(PlacedFeature var1) {
      return this.featureSet.get().contains(var1);
   }

   public static class Builder extends BiomeGenerationSettings.PlainBuilder {
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<ConfiguredWorldCarver<?>> worldCarvers;

      public Builder(HolderGetter<PlacedFeature> var1, HolderGetter<ConfiguredWorldCarver<?>> var2) {
         super();
         this.placedFeatures = var1;
         this.worldCarvers = var2;
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration var1, ResourceKey<PlacedFeature> var2) {
         this.addFeature(var1.ordinal(), this.placedFeatures.getOrThrow(var2));
         return this;
      }

      public BiomeGenerationSettings.Builder addCarver(ResourceKey<ConfiguredWorldCarver<?>> var1) {
         this.addCarver(this.worldCarvers.getOrThrow(var1));
         return this;
      }
   }

   public static class PlainBuilder {
      private final List<Holder<ConfiguredWorldCarver<?>>> carvers = new ArrayList<>();
      private final List<List<Holder<PlacedFeature>>> features = new ArrayList<>();

      public PlainBuilder() {
         super();
      }

      public BiomeGenerationSettings.PlainBuilder addFeature(GenerationStep.Decoration var1, Holder<PlacedFeature> var2) {
         return this.addFeature(var1.ordinal(), var2);
      }

      public BiomeGenerationSettings.PlainBuilder addFeature(int var1, Holder<PlacedFeature> var2) {
         this.addFeatureStepsUpTo(var1);
         this.features.get(var1).add(var2);
         return this;
      }

      public BiomeGenerationSettings.PlainBuilder addCarver(Holder<ConfiguredWorldCarver<?>> var1) {
         this.carvers.add(var1);
         return this;
      }

      private void addFeatureStepsUpTo(int var1) {
         while (this.features.size() <= var1) {
            this.features.add(Lists.newArrayList());
         }
      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings(
            HolderSet.direct(this.carvers), this.features.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList())
         );
      }
   }
}
