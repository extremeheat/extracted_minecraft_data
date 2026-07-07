package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface BlockStateProviderTypes {
   static MapCodec<? extends BlockStateProvider> bootstrap(final Registry<MapCodec<? extends BlockStateProvider>> registry) {
      Registry.register(registry, (String)"copy_properties_provider", CopyPropertiesProvider.CODEC);
      Registry.register(registry, (String)"dual_noise_provider", DualNoiseProvider.CODEC);
      Registry.register(registry, (String)"noise_provider", NoiseProvider.CODEC);
      Registry.register(registry, (String)"noise_threshold_provider", NoiseThresholdProvider.CODEC);
      Registry.register(registry, (String)"random_block_provider", RandomBlockProvider.CODEC);
      Registry.register(registry, (String)"randomized_int_state_provider", RandomizedIntStateProvider.CODEC);
      Registry.register(registry, (String)"rotated_block_provider", RotatedBlockProvider.CODEC);
      Registry.register(registry, (String)"rule_based_state_provider", RuleBasedStateProvider.CODEC);
      Registry.register(registry, (String)"simple_state_provider", SimpleStateProvider.CODEC);
      return (MapCodec)Registry.register(registry, (String)"weighted_state_provider", WeightedStateProvider.CODEC);
   }
}
