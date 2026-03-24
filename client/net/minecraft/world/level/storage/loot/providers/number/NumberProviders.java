package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class NumberProviders {
   private static final Codec<NumberProvider> TYPED_CODEC;
   public static final Codec<NumberProvider> CODEC;

   public NumberProviders() {
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
      return (MapCodec)Registry.register(registry, (String)"environment_attribute", EnvironmentAttributeValue.MAP_CODEC);
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.byNameCodec().dispatch(NumberProvider::codec, (c) -> c);
      CODEC = Codec.lazyInitialized(() -> {
         Codec<NumberProvider> typedCodecWithFallback = Codec.withAlternative(TYPED_CODEC, UniformGenerator.MAP_CODEC.codec());
         return Codec.either(ConstantValue.INLINE_CODEC, typedCodecWithFallback).xmap(Either::unwrap, (provider) -> {
            Either var10000;
            if (provider instanceof ConstantValue constant) {
               var10000 = Either.left(constant);
            } else {
               var10000 = Either.right(provider);
            }

            return var10000;
         });
      });
   }
}
