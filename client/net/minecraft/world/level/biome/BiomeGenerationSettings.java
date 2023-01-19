package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public class BiomeGenerationSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(ImmutableMap.of(), ImmutableList.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.simpleMap(
                     GenerationStep.Carving.CODEC,
                     ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", LOGGER::error)),
                     StringRepresentable.keys(GenerationStep.Carving.values())
                  )
                  .fieldOf("carvers")
                  .forGetter(var0x -> var0x.carvers),
               PlacedFeature.LIST_OF_LISTS_CODEC
                  .promotePartial(Util.prefix("Features: ", LOGGER::error))
                  .fieldOf("features")
                  .forGetter(var0x -> var0x.features)
            )
            .apply(var0, BiomeGenerationSettings::new)
   );
   private final Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers;
   private final List<HolderSet<PlacedFeature>> features;
   private final Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures;
   private final Supplier<Set<PlacedFeature>> featureSet;

   BiomeGenerationSettings(Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> var1, List<HolderSet<PlacedFeature>> var2) {
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

   public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving var1) {
      return Objects.requireNonNullElseGet(this.carvers.get(var1), List::of);
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

   public static class Builder {
      private final Map<GenerationStep.Carving, List<Holder<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
      private final List<List<Holder<PlacedFeature>>> features = Lists.newArrayList();

      public Builder() {
         super();
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration var1, Holder<PlacedFeature> var2) {
         return this.addFeature(var1.ordinal(), var2);
      }

      public BiomeGenerationSettings.Builder addFeature(int var1, Holder<PlacedFeature> var2) {
         this.addFeatureStepsUpTo(var1);
         this.features.get(var1).add(var2);
         return this;
      }

      public BiomeGenerationSettings.Builder addCarver(GenerationStep.Carving var1, Holder<? extends ConfiguredWorldCarver<?>> var2) {
         this.carvers.computeIfAbsent(var1, var0 -> Lists.newArrayList()).add(Holder.hackyErase(var2));
         return this;
      }

      private void addFeatureStepsUpTo(int var1) {
         while(this.features.size() <= var1) {
            this.features.add(Lists.newArrayList());
         }
      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings(
            this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, var0 -> HolderSet.direct((List)var0.getValue()))),
            this.features.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList())
         );
      }
   }
}
