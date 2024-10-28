package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> var0) {
      AquaticFeatures.bootstrap(var0);
      CaveFeatures.bootstrap(var0);
      EndFeatures.bootstrap(var0);
      MiscOverworldFeatures.bootstrap(var0);
      NetherFeatures.bootstrap(var0);
      OreFeatures.bootstrap(var0);
      PileFeatures.bootstrap(var0);
      TreeFeatures.bootstrap(var0);
      VegetationFeatures.bootstrap(var0);
   }

   private static BlockPredicate simplePatchPredicate(List<Block> var0) {
      BlockPredicate var1;
      if (!var0.isEmpty()) {
         var1 = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), var0));
      } else {
         var1 = BlockPredicate.ONLY_IN_AIR_PREDICATE;
      }

      return var1;
   }

   public static RandomPatchConfiguration simpleRandomPatchConfiguration(int var0, Holder<PlacedFeature> var1) {
      return new RandomPatchConfiguration(var0, 7, 3, var1);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F var0, FC var1, List<Block> var2, int var3) {
      return simpleRandomPatchConfiguration(var3, PlacementUtils.filtered(var0, var1, simplePatchPredicate(var2)));
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F var0, FC var1, List<Block> var2) {
      return simplePatchConfiguration(var0, var1, var2, 96);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F var0, FC var1) {
      return simplePatchConfiguration(var0, var1, List.of(), 96);
   }

   public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String var0) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void register(BootstrapContext<ConfiguredFeature<?, ?>> var0, ResourceKey<ConfiguredFeature<?, ?>> var1, Feature<NoneFeatureConfiguration> var2) {
      register(var0, var1, var2, FeatureConfiguration.NONE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> var0, ResourceKey<ConfiguredFeature<?, ?>> var1, F var2, FC var3) {
      var0.register(var1, new ConfiguredFeature(var2, var3));
   }
}
