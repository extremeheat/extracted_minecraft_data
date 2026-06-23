package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FeatureTags;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public class BiomeGenerationSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(HolderSet.empty(), List.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec((i) -> {
      Codec var10001 = ConfiguredWorldCarver.LIST_CODEC;
      Logger var10003 = LOGGER;
      Objects.requireNonNull(var10003);
      RecordCodecBuilder var1 = var10001.promotePartial(Util.prefix("Carver: ", var10003::error)).fieldOf("carvers").forGetter((b) -> b.carvers);
      Codec var10002 = PlacedFeature.LIST_OF_LISTS_CODEC;
      Logger var10004 = LOGGER;
      Objects.requireNonNull(var10004);
      return i.group(var1, var10002.promotePartial(Util.prefix("Features: ", var10004::error)).fieldOf("features").forGetter((b) -> b.features)).apply(i, BiomeGenerationSettings::new);
   });
   private final HolderSet<ConfiguredWorldCarver<?>> carvers;
   private final List<HolderSet<PlacedFeature>> features;
   private final Supplier<List<Feature>> boneMealFeatures;
   private final Supplier<Set<PlacedFeature>> featureSet;

   private BiomeGenerationSettings(final HolderSet<ConfiguredWorldCarver<?>> carvers, final List<HolderSet<PlacedFeature>> features) {
      super();
      this.carvers = carvers;
      this.features = features;
      this.boneMealFeatures = Suppliers.memoize(() -> (List)features.stream().flatMap(HolderSet::stream).flatMap((feature) -> ((PlacedFeature)feature.value()).getFeatures()).filter((feature) -> feature.is(FeatureTags.CAN_SPAWN_FROM_BONE_MEAL)).map(Holder::value).collect(ImmutableList.toImmutableList()));
      this.featureSet = Suppliers.memoize(() -> (Set)features.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet()));
   }

   public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers() {
      return this.carvers;
   }

   public List<Feature> getBoneMealFeatures() {
      return (List)this.boneMealFeatures.get();
   }

   public List<HolderSet<PlacedFeature>> features() {
      return this.features;
   }

   public boolean hasFeature(final PlacedFeature feature) {
      return ((Set)this.featureSet.get()).contains(feature);
   }

   public static class PlainBuilder {
      private final List<Holder<ConfiguredWorldCarver<?>>> carvers = new ArrayList();
      private final List<List<Holder<PlacedFeature>>> features = new ArrayList();

      public PlainBuilder() {
         super();
      }

      public PlainBuilder addFeature(final GenerationStep.Decoration step, final Holder<PlacedFeature> feature) {
         return this.addFeature(step.ordinal(), feature);
      }

      public PlainBuilder addFeature(final int index, final Holder<PlacedFeature> feature) {
         this.addFeatureStepsUpTo(index);
         ((List)this.features.get(index)).add(feature);
         return this;
      }

      public PlainBuilder addCarver(final Holder<ConfiguredWorldCarver<?>> carver) {
         this.carvers.add(carver);
         return this;
      }

      private void addFeatureStepsUpTo(final int index) {
         while(this.features.size() <= index) {
            this.features.add(Lists.newArrayList());
         }

      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings(HolderSet.direct(this.carvers), (List)this.features.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList()));
      }
   }

   public static class Builder extends PlainBuilder {
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<ConfiguredWorldCarver<?>> worldCarvers;

      public Builder(final HolderGetter<PlacedFeature> placedFeatures, final HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
         super();
         this.placedFeatures = placedFeatures;
         this.worldCarvers = worldCarvers;
      }

      public Builder addFeature(final GenerationStep.Decoration step, final ResourceKey<PlacedFeature> feature) {
         this.addFeature(step.ordinal(), this.placedFeatures.getOrThrow(feature));
         return this;
      }

      public Builder addCarver(final ResourceKey<ConfiguredWorldCarver<?>> carver) {
         this.addCarver(this.worldCarvers.getOrThrow(carver));
         return this;
      }
   }
}
