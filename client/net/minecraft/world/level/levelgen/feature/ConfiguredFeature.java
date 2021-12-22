package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>> {
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

   public PlacedFeature placed(List<PlacementModifier> var1) {
      return new PlacedFeature(() -> {
         return this;
      }, var1);
   }

   public PlacedFeature placed(PlacementModifier... var1) {
      return this.placed(List.of(var1));
   }

   public PlacedFeature filteredByBlockSurvival(Block var1) {
      return this.filtered(BlockPredicate.wouldSurvive(var1.defaultBlockState(), BlockPos.ZERO));
   }

   public PlacedFeature onlyWhenEmpty() {
      return this.filtered(BlockPredicate.matchesBlock(Blocks.AIR, BlockPos.ZERO));
   }

   public PlacedFeature filtered(BlockPredicate var1) {
      return this.placed(BlockPredicateFilter.forPredicate(var1));
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4) {
      return var1.ensureCanWrite(var4) ? this.feature.place(new FeaturePlaceContext(Optional.empty(), var1, var2, var3, var4, this.config)) : false;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(Stream.of(this), this.config.getFeatures());
   }

   public String toString() {
      return (String)BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(this).map(Objects::toString).orElseGet(() -> {
         return DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, this).toString();
      });
   }

   static {
      DIRECT_CODEC = Registry.FEATURE.byNameCodec().dispatch((var0) -> {
         return var0.feature;
      }, Feature::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
      LOGGER = LogManager.getLogger();
   }
}
