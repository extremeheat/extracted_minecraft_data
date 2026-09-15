package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface BlockStateProviderTypes {
   static MapCodec<? extends BlockStateProvider> bootstrap(final Registry<MapCodec<? extends BlockStateProvider>> registry) {
      Registry.register(registry, (String)"copy_properties", CopyPropertiesProvider.CODEC);
      Registry.register(registry, (String)"dual_noise", DualNoiseProvider.CODEC);
      Registry.register(registry, (String)"noise", NoiseProvider.CODEC);
      Registry.register(registry, (String)"noise_threshold", NoiseThresholdProvider.CODEC);
      Registry.register(registry, (String)"random_block", RandomBlockProvider.CODEC);
      Registry.register(registry, (String)"randomized_int", RandomizedIntStateProvider.CODEC);
      Registry.register(registry, (String)"rotated", RotatedBlockProvider.CODEC);
      Registry.register(registry, (String)"rule_based", RuleBasedStateProvider.CODEC);
      Registry.register(registry, (String)"simple", SimpleStateProvider.CODEC);
      return (MapCodec)Registry.register(registry, (String)"weighted", WeightedStateProvider.CODEC);
   }
}
