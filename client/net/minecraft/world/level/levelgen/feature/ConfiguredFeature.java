package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Decoratable;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>> implements Decoratable<ConfiguredFeature<?, ?>> {
   public static final Codec<ConfiguredFeature<?, ?>> DIRECT_CODEC;
   public static final Codec<Supplier<ConfiguredFeature<?, ?>>> CODEC;
   public static final Codec<List<Supplier<ConfiguredFeature<?, ?>>>> LIST_CODEC;
   public static final Logger LOGGER;
   public final F feature;
   public final FC config;

   public ConfiguredFeature(F var1, FC var2) {
      super();
      this.feature = var1;
      this.config = var2;
   }

   public F feature() {
      return this.feature;
   }

   public FC config() {
      return this.config;
   }

   public ConfiguredFeature<?, ?> decorated(ConfiguredDecorator<?> var1) {
      return Feature.DECORATED.configured(new DecoratedFeatureConfiguration(() -> {
         return this;
      }, var1));
   }

   public WeightedConfiguredFeature weighted(float var1) {
      return new WeightedConfiguredFeature(this, var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4) {
      return this.feature.place(var1, var2, var3, var4, this.config);
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(Stream.of(this), this.config.getFeatures());
   }

   // $FF: synthetic method
   public Object decorated(ConfiguredDecorator var1) {
      return this.decorated(var1);
   }

   static {
      DIRECT_CODEC = Registry.FEATURE.dispatch((var0) -> {
         return var0.feature;
      }, Feature::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
      LOGGER = LogManager.getLogger();
   }
}
