package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
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

   public static Holder<? extends ConfiguredFeature<?, ?>> bootstrap(Registry<ConfiguredFeature<?, ?>> var0) {
      List var1 = List.of(AquaticFeatures.KELP, CaveFeatures.MOSS_PATCH_BONEMEAL, EndFeatures.CHORUS_PLANT, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, NetherFeatures.BASALT_BLOBS, OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileFeatures.PILE_HAY, TreeFeatures.AZALEA_TREE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
      return (Holder)Util.getRandom(var1, RandomSource.create());
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

   public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> register(String var0, Feature<NoneFeatureConfiguration> var1) {
      return register(var0, var1, FeatureConfiguration.NONE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String var0, F var1, FC var2) {
      return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, var0, new ConfiguredFeature(var1, var2));
   }
}
