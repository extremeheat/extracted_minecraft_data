package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>>(F d, FC e) {
   private final F feature;
   private final FC config;
   public static final Codec<ConfiguredFeature<?, ?>> DIRECT_CODEC;
   public static final Codec<Holder<ConfiguredFeature<?, ?>>> CODEC;
   public static final Codec<HolderSet<ConfiguredFeature<?, ?>>> LIST_CODEC;

   public ConfiguredFeature(F var1, FC var2) {
      super();
      this.feature = var1;
      this.config = var2;
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, RandomSource var3, BlockPos var4) {
      return this.feature.place(this.config, var1, var2, var3, var4);
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(Stream.of(this), this.config.getFeatures());
   }

   public String toString() {
      return "Configured: " + this.feature + ": " + this.config;
   }

   public F feature() {
      return this.feature;
   }

   public FC config() {
      return this.config;
   }

   static {
      DIRECT_CODEC = Registry.FEATURE.byNameCodec().dispatch((var0) -> {
         return var0.feature;
      }, Feature::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.homogeneousList(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
   }
}
