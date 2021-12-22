package net.minecraft.data.worldgen.features;

import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {
   public FeatureUtils() {
      super();
   }

   public static ConfiguredFeature<?, ?> bootstrap() {
      ConfiguredFeature[] var0 = new ConfiguredFeature[]{AquaticFeatures.KELP, CaveFeatures.MOSS_PATCH_BONEMEAL, EndFeatures.CHORUS_PLANT, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, NetherFeatures.BASALT_BLOBS, OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileFeatures.PILE_HAY, TreeFeatures.AZALEA_TREE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA};
      return (ConfiguredFeature)Util.getRandom((Object[])var0, new Random());
   }

   private static BlockPredicate simplePatchPredicate(List<Block> var0) {
      BlockPredicate var1;
      if (!var0.isEmpty()) {
         var1 = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(var0, new BlockPos(0, -1, 0)));
      } else {
         var1 = BlockPredicate.ONLY_IN_AIR_PREDICATE;
      }

      return var1;
   }

   public static RandomPatchConfiguration simpleRandomPatchConfiguration(int var0, PlacedFeature var1) {
      return new RandomPatchConfiguration(var0, 7, 3, () -> {
         return var1;
      });
   }

   public static RandomPatchConfiguration simplePatchConfiguration(ConfiguredFeature<?, ?> var0, List<Block> var1, int var2) {
      return simpleRandomPatchConfiguration(var2, var0.filtered(simplePatchPredicate(var1)));
   }

   public static RandomPatchConfiguration simplePatchConfiguration(ConfiguredFeature<?, ?> var0, List<Block> var1) {
      return simplePatchConfiguration(var0, var1, 96);
   }

   public static RandomPatchConfiguration simplePatchConfiguration(ConfiguredFeature<?, ?> var0) {
      return simplePatchConfiguration(var0, List.of(), 96);
   }

   public static <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> register(String var0, ConfiguredFeature<FC, ?> var1) {
      return (ConfiguredFeature)Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, (String)var0, var1);
   }
}
