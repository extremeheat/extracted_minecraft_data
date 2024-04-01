package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class NumberProviders {
   private static final Codec<NumberProvider> TYPED_CODEC = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE
      .byNameCodec()
      .dispatch(NumberProvider::getType, LootNumberProviderType::codec);
   public static final Codec<NumberProvider> CODEC = ExtraCodecs.lazyInitializedCodec(
      () -> {
         Codec var0 = ExtraCodecs.withAlternative(TYPED_CODEC, UniformGenerator.CODEC);
         return Codec.either(ConstantValue.INLINE_CODEC, var0)
            .xmap(
               var0x -> (NumberProvider)var0x.map(Function.identity(), Function.identity()),
               var0x -> var0x instanceof ConstantValue var1 ? Either.left(var1) : Either.right(var0x)
            );
      }
   );
   public static final LootNumberProviderType CONSTANT = register("constant", ConstantValue.CODEC);
   public static final LootNumberProviderType UNIFORM = register("uniform", UniformGenerator.CODEC);
   public static final LootNumberProviderType BINOMIAL = register("binomial", BinomialDistributionGenerator.CODEC);
   public static final LootNumberProviderType SCORE = register("score", ScoreboardValue.CODEC);
   public static final LootNumberProviderType STORAGE = register("storage", StorageValue.CODEC);

   public NumberProviders() {
      super();
   }

   private static LootNumberProviderType register(String var0, Codec<? extends NumberProvider> var1) {
      return Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, new ResourceLocation(var0), new LootNumberProviderType(var1));
   }
}
