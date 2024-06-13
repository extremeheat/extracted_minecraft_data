package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>>(F feature, FC config) {
   public static final Codec<ConfiguredFeature<?, ?>> DIRECT_CODEC = BuiltInRegistries.FEATURE
      .byNameCodec()
      .dispatch(var0 -> var0.feature, Feature::configuredCodec);
   public static final Codec<Holder<ConfiguredFeature<?, ?>>> CODEC = RegistryFileCodec.create(Registries.CONFIGURED_FEATURE, DIRECT_CODEC);
   public static final Codec<HolderSet<ConfiguredFeature<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.CONFIGURED_FEATURE, DIRECT_CODEC);

   public ConfiguredFeature(F feature, FC config) {
      super();
      this.feature = (F)feature;
      this.config = (FC)config;
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
}
