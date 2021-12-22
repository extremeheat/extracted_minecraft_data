package net.minecraft.data.worldgen.features;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class PileFeatures {
   public static final ConfiguredFeature<?, ?> PILE_HAY;
   public static final ConfiguredFeature<?, ?> PILE_MELON;
   public static final ConfiguredFeature<?, ?> PILE_SNOW;
   public static final ConfiguredFeature<?, ?> PILE_ICE;
   public static final ConfiguredFeature<?, ?> PILE_PUMPKIN;

   public PileFeatures() {
      super();
   }

   static {
      PILE_HAY = FeatureUtils.register("pile_hay", Feature.BLOCK_PILE.configured(new BlockPileConfiguration(new RotatedBlockProvider(Blocks.HAY_BLOCK))));
      PILE_MELON = FeatureUtils.register("pile_melon", Feature.BLOCK_PILE.configured(new BlockPileConfiguration(BlockStateProvider.simple(Blocks.MELON))));
      PILE_SNOW = FeatureUtils.register("pile_snow", Feature.BLOCK_PILE.configured(new BlockPileConfiguration(BlockStateProvider.simple(Blocks.SNOW))));
      PILE_ICE = FeatureUtils.register("pile_ice", Feature.BLOCK_PILE.configured(new BlockPileConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.BLUE_ICE.defaultBlockState(), 1).add(Blocks.PACKED_ICE.defaultBlockState(), 5)))));
      PILE_PUMPKIN = FeatureUtils.register("pile_pumpkin", Feature.BLOCK_PILE.configured(new BlockPileConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.PUMPKIN.defaultBlockState(), 19).add(Blocks.JACK_O_LANTERN.defaultBlockState(), 1)))));
   }
}
