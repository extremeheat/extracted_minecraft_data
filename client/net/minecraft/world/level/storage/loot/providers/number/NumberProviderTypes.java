package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class NumberProviderTypes {
   public NumberProviderTypes() {
      super();
   }

   public static MapCodec<? extends NumberProvider> bootstrap(final Registry<MapCodec<? extends NumberProvider>> registry) {
      Registry.register(registry, (String)"constant", ConstantValue.MAP_CODEC);
      Registry.register(registry, (String)"uniform", UniformGenerator.MAP_CODEC);
      Registry.register(registry, (String)"binomial", BinomialDistributionGenerator.MAP_CODEC);
      Registry.register(registry, (String)"score", ScoreboardValue.MAP_CODEC);
      Registry.register(registry, (String)"storage", StorageValue.MAP_CODEC);
      Registry.register(registry, (String)"sum", Sum.MAP_CODEC);
      Registry.register(registry, (String)"enchantment_level", EnchantmentLevelProvider.MAP_CODEC);
      Registry.register(registry, (String)"weighted_list", WeightedListValue.MAP_CODEC);
      Registry.register(registry, (String)"conditional", ConditionalValue.MAP_CODEC);
      Registry.register(registry, (String)"number_dispatcher", NumberDispatcher.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"environment_attribute", EnvironmentAttributeValue.MAP_CODEC);
   }
}
