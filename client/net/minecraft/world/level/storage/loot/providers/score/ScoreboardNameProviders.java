package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class ScoreboardNameProviders {
   private static final Codec<ScoreboardNameProvider> TYPED_CODEC = BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE
      .byNameCodec()
      .dispatch(ScoreboardNameProvider::getType, LootScoreProviderType::codec);
   public static final Codec<ScoreboardNameProvider> CODEC = ExtraCodecs.lazyInitializedCodec(
      () -> Codec.either(ContextScoreboardNameProvider.INLINE_CODEC, TYPED_CODEC)
            .xmap(
               var0 -> (ScoreboardNameProvider)var0.map(Function.identity(), Function.identity()),
               var0 -> var0 instanceof ContextScoreboardNameProvider var1 ? Either.left(var1) : Either.right(var0)
            )
   );
   public static final LootScoreProviderType FIXED = register("fixed", FixedScoreboardNameProvider.CODEC);
   public static final LootScoreProviderType CONTEXT = register("context", ContextScoreboardNameProvider.CODEC);

   public ScoreboardNameProviders() {
      super();
   }

   private static LootScoreProviderType register(String var0, Codec<? extends ScoreboardNameProvider> var1) {
      return Registry.register(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, new ResourceLocation(var0), new LootScoreProviderType(var1));
   }
}
