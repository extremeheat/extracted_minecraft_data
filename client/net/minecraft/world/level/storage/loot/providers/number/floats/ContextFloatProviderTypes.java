package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class ContextFloatProviderTypes {
   public ContextFloatProviderTypes() {
      super();
   }

   public static MapCodec<? extends ContextFloatProvider> bootstrap(final Registry<MapCodec<? extends ContextFloatProvider>> registry) {
      Registry.register(registry, (String)"abs", Absolute.MAP_CODEC);
      Registry.register(registry, (String)"avg", Average.MAP_CODEC);
      Registry.register(registry, (String)"ceil", Ceiling.MAP_CODEC);
      Registry.register(registry, (String)"conditional", ConditionalValue.MAP_CODEC);
      Registry.register(registry, (String)"constant", ConstantValue.MAP_CODEC);
      Registry.register(registry, (String)"cos", Cosine.MAP_CODEC);
      Registry.register(registry, (String)"sub", Difference.MAP_CODEC);
      Registry.register(registry, (String)"enchantment_level", EnchantmentLevelProvider.MAP_CODEC);
      Registry.register(registry, (String)"environment_attribute", EnvironmentAttributeValue.MAP_CODEC);
      Registry.register(registry, (String)"floor", Floor.MAP_CODEC);
      Registry.register(registry, (String)"from_int", FromInt.MAP_CODEC);
      Registry.register(registry, (String)"length", Length.MAP_CODEC);
      Registry.register(registry, (String)"max", Maximum.MAP_CODEC);
      Registry.register(registry, (String)"min", Minimum.MAP_CODEC);
      Registry.register(registry, (String)"mod", Modulus.MAP_CODEC);
      Registry.register(registry, (String)"negate", Negate.MAP_CODEC);
      Registry.register(registry, (String)"number_dispatcher", NumberDispatcher.MAP_CODEC);
      Registry.register(registry, (String)"pow", Power.MAP_CODEC);
      Registry.register(registry, (String)"mul", Product.MAP_CODEC);
      Registry.register(registry, (String)"div", Quotient.MAP_CODEC);
      Registry.register(registry, (String)"round", Round.MAP_CODEC);
      Registry.register(registry, (String)"sin", Sine.MAP_CODEC);
      Registry.register(registry, (String)"sqrt", SquareRoot.MAP_CODEC);
      Registry.register(registry, (String)"storage", StorageValue.MAP_CODEC);
      Registry.register(registry, (String)"add", Sum.MAP_CODEC);
      Registry.register(registry, (String)"truncate", Truncate.MAP_CODEC);
      Registry.register(registry, (String)"uniform", UniformGenerator.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"weighted_list", WeightedListValue.MAP_CODEC);
   }
}
