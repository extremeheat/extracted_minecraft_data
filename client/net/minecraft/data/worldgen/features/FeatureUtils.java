package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {
   public FeatureUtils() {
      super();
   }

   public static void bootstrap(final BootstrapContext<ConfiguredFeature<?, ?>> context) {
      AquaticFeatures.bootstrap(context);
      CaveFeatures.bootstrap(context);
      EndFeatures.bootstrap(context);
      MiscOverworldFeatures.bootstrap(context);
      NetherFeatures.bootstrap(context);
      OreFeatures.bootstrap(context);
      PileFeatures.bootstrap(context);
      TreeFeatures.bootstrap(context);
      VegetationFeatures.bootstrap(context);
   }

   private static BlockPredicate simplePatchPredicate(final List<Block> allowedOn) {
      BlockPredicate predicate;
      if (!allowedOn.isEmpty()) {
         predicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(), allowedOn));
      } else {
         predicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
      }

      return predicate;
   }

   public static RandomPatchConfiguration simpleRandomPatchConfiguration(final int tries, final Holder<PlacedFeature> feature) {
      return new RandomPatchConfiguration(tries, 7, 3, feature);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(final F feature, final FC config, final List<Block> allowedOn, final int tries) {
      return simpleRandomPatchConfiguration(tries, PlacementUtils.filtered(feature, config, simplePatchPredicate(allowedOn)));
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(final F feature, final FC config, final List<Block> allowedOn) {
      return simplePatchConfiguration(feature, config, allowedOn, 96);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(final F feature, final FC config) {
      return simplePatchConfiguration(feature, config, List.of(), 96);
   }

   public static ResourceKey<ConfiguredFeature<?, ?>> createKey(final String name) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.withDefaultNamespace(name));
   }

   public static void register(final BootstrapContext<ConfiguredFeature<?, ?>> context, final ResourceKey<ConfiguredFeature<?, ?>> id, final Feature<NoneFeatureConfiguration> feature) {
      register(context, id, feature, FeatureConfiguration.NONE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(final BootstrapContext<ConfiguredFeature<?, ?>> context, final ResourceKey<ConfiguredFeature<?, ?>> id, final F feature, final FC config) {
      context.register(id, new ConfiguredFeature(feature, config));
   }
}
