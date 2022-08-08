package net.minecraft.world.level.storage.loot.providers.score;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

public class ScoreboardNameProviders {
   public static final LootScoreProviderType FIXED = register("fixed", new FixedScoreboardNameProvider.Serializer());
   public static final LootScoreProviderType CONTEXT = register("context", new ContextScoreboardNameProvider.Serializer());

   public ScoreboardNameProviders() {
      super();
   }

   private static LootScoreProviderType register(String var0, Serializer<? extends ScoreboardNameProvider> var1) {
      return (LootScoreProviderType)Registry.register(Registry.LOOT_SCORE_PROVIDER_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new LootScoreProviderType(var1));
   }

   public static Object createGsonAdapter() {
      return GsonAdapterFactory.builder(Registry.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", ScoreboardNameProvider::getType).withInlineSerializer(CONTEXT, new ContextScoreboardNameProvider.InlineSerializer()).build();
   }
}
