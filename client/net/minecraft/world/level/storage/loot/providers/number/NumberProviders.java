package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class NumberProviders {
   private static final Codec<NumberProvider> TYPED_CODEC;
   public static final Codec<NumberProvider> CODEC;
   public static final LootNumberProviderType CONSTANT;
   public static final LootNumberProviderType UNIFORM;
   public static final LootNumberProviderType BINOMIAL;
   public static final LootNumberProviderType SCORE;
   public static final LootNumberProviderType STORAGE;
   public static final LootNumberProviderType ENCHANTMENT_LEVEL;

   public NumberProviders() {
      super();
   }

   private static LootNumberProviderType register(String var0, MapCodec<? extends NumberProvider> var1) {
      return (LootNumberProviderType)Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new LootNumberProviderType(var1));
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.byNameCodec().dispatch(NumberProvider::getType, LootNumberProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> {
         Codec var0 = Codec.withAlternative(TYPED_CODEC, UniformGenerator.CODEC.codec());
         return Codec.either(ConstantValue.INLINE_CODEC, var0).xmap(Either::unwrap, (var0x) -> {
            Either var10000;
            if (var0x instanceof ConstantValue var1) {
               var10000 = Either.left(var1);
            } else {
               var10000 = Either.right(var0x);
            }

            return var10000;
         });
      });
      CONSTANT = register("constant", ConstantValue.CODEC);
      UNIFORM = register("uniform", UniformGenerator.CODEC);
      BINOMIAL = register("binomial", BinomialDistributionGenerator.CODEC);
      SCORE = register("score", ScoreboardValue.CODEC);
      STORAGE = register("storage", StorageValue.CODEC);
      ENCHANTMENT_LEVEL = register("enchantment_level", EnchantmentLevelProvider.CODEC);
   }
}
