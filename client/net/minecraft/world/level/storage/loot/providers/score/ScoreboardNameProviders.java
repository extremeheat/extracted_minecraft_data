package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ScoreboardNameProviders {
   private static final Codec<ScoreboardNameProvider> TYPED_CODEC;
   public static final Codec<ScoreboardNameProvider> CODEC;
   public static final LootScoreProviderType FIXED;
   public static final LootScoreProviderType CONTEXT;

   public ScoreboardNameProviders() {
      super();
   }

   private static LootScoreProviderType register(String var0, MapCodec<? extends ScoreboardNameProvider> var1) {
      return (LootScoreProviderType)Registry.register(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new LootScoreProviderType(var1));
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE.byNameCodec().dispatch(ScoreboardNameProvider::getType, LootScoreProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> Codec.either(ContextScoreboardNameProvider.INLINE_CODEC, TYPED_CODEC).xmap(Either::unwrap, (var0) -> {
            Either var10000;
            if (var0 instanceof ContextScoreboardNameProvider var1) {
               var10000 = Either.left(var1);
            } else {
               var10000 = Either.right(var0);
            }

            return var10000;
         }));
      FIXED = register("fixed", FixedScoreboardNameProvider.CODEC);
      CONTEXT = register("context", ContextScoreboardNameProvider.CODEC);
   }
}
