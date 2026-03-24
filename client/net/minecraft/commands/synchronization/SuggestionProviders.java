package net.minecraft.commands.synchronization;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
   private static final Map<Identifier, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = new HashMap();
   private static final Identifier ID_ASK_SERVER = Identifier.withDefaultNamespace("ask_server");
   public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER;
   public static final SuggestionProvider<SharedSuggestionProvider> AVAILABLE_SOUNDS;
   public static final SuggestionProvider<SharedSuggestionProvider> SUMMONABLE_ENTITIES;

   public SuggestionProviders() {
      super();
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(final Identifier name, final SuggestionProvider<SharedSuggestionProvider> provider) {
      SuggestionProvider<SharedSuggestionProvider> previous = (SuggestionProvider)PROVIDERS_BY_NAME.putIfAbsent(name, provider);
      if (previous != null) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name '" + String.valueOf(name) + "'");
      } else {
         return new RegisteredSuggestion(name, provider);
      }
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> cast(final SuggestionProvider<SharedSuggestionProvider> provider) {
      return provider;
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> getProvider(final Identifier name) {
      return cast((SuggestionProvider)PROVIDERS_BY_NAME.getOrDefault(name, ASK_SERVER));
   }

   public static Identifier getName(final SuggestionProvider<?> provider) {
      Identifier var10000;
      if (provider instanceof RegisteredSuggestion registeredProvider) {
         var10000 = registeredProvider.name;
      } else {
         var10000 = ID_ASK_SERVER;
      }

      return var10000;
   }

   static {
      ASK_SERVER = register(ID_ASK_SERVER, (c, p) -> ((SharedSuggestionProvider)c.getSource()).customSuggestion(c));
      AVAILABLE_SOUNDS = register(Identifier.withDefaultNamespace("available_sounds"), (c, p) -> SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)c.getSource()).getAvailableSounds(), p));
      SUMMONABLE_ENTITIES = register(Identifier.withDefaultNamespace("summonable_entities"), (c, p) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream().filter((entityType) -> entityType.isEnabled(((SharedSuggestionProvider)c.getSource()).enabledFeatures()) && entityType.canSummon()), p, EntityType::getKey, EntityType::getDescription));
   }

   private static record RegisteredSuggestion(Identifier name, SuggestionProvider<SharedSuggestionProvider> delegate) implements SuggestionProvider<SharedSuggestionProvider> {
      private RegisteredSuggestion {
         super();
      }

      public CompletableFuture<Suggestions> getSuggestions(final CommandContext<SharedSuggestionProvider> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
         return this.delegate.getSuggestions(context, builder);
      }
   }
}
