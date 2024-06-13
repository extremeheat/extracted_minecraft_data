package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ScoreboardNameProviders {
   private static final Codec<ScoreboardNameProvider> TYPED_CODEC = BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE
      .byNameCodec()
      .dispatch(ScoreboardNameProvider::getType, LootScoreProviderType::codec);
   public static final Codec<ScoreboardNameProvider> CODEC = Codec.lazyInitialized(
      () -> Codec.either(ContextScoreboardNameProvider.INLINE_CODEC, TYPED_CODEC)
            .xmap(Either::unwrap, var0 -> var0 instanceof ContextScoreboardNameProvider var1 ? Either.left(var1) : Either.right(var0))
   );
   public static final LootScoreProviderType FIXED = register("fixed", FixedScoreboardNameProvider.CODEC);
   public static final LootScoreProviderType CONTEXT = register("context", ContextScoreboardNameProvider.CODEC);

   public ScoreboardNameProviders() {
      super();
   }

   private static LootScoreProviderType register(String var0, MapCodec<? extends ScoreboardNameProvider> var1) {
      return Registry.register(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, ResourceLocation.withDefaultNamespace(var0), new LootScoreProviderType(var1));
   }
}
