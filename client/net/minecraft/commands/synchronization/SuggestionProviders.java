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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = new HashMap();
   private static final ResourceLocation ID_ASK_SERVER = ResourceLocation.withDefaultNamespace("ask_server");
   public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER;
   public static final SuggestionProvider<SharedSuggestionProvider> AVAILABLE_SOUNDS;
   public static final SuggestionProvider<SharedSuggestionProvider> SUMMONABLE_ENTITIES;

   public SuggestionProviders() {
      super();
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(ResourceLocation var0, SuggestionProvider<SharedSuggestionProvider> var1) {
      SuggestionProvider var2 = (SuggestionProvider)PROVIDERS_BY_NAME.putIfAbsent(var0, var1);
      if (var2 != null) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name '" + String.valueOf(var0) + "'");
      } else {
         return new RegisteredSuggestion(var0, var1);
      }
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> cast(SuggestionProvider<SharedSuggestionProvider> var0) {
      return var0;
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> getProvider(ResourceLocation var0) {
      return cast((SuggestionProvider)PROVIDERS_BY_NAME.getOrDefault(var0, ASK_SERVER));
   }

   public static ResourceLocation getName(SuggestionProvider<?> var0) {
      ResourceLocation var10000;
      if (var0 instanceof RegisteredSuggestion var1) {
         var10000 = var1.name;
      } else {
         var10000 = ID_ASK_SERVER;
      }

      return var10000;
   }

   static {
      ASK_SERVER = register(ID_ASK_SERVER, (var0, var1) -> ((SharedSuggestionProvider)var0.getSource()).customSuggestion(var0));
      AVAILABLE_SOUNDS = register(ResourceLocation.withDefaultNamespace("available_sounds"), (var0, var1) -> SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)var0.getSource()).getAvailableSounds(), var1));
      SUMMONABLE_ENTITIES = register(ResourceLocation.withDefaultNamespace("summonable_entities"), (var0, var1) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream().filter((var1x) -> var1x.isEnabled(((SharedSuggestionProvider)var0.getSource()).enabledFeatures()) && var1x.canSummon()), var1, EntityType::getKey, EntityType::getDescription));
   }

   static record RegisteredSuggestion(ResourceLocation name, SuggestionProvider<SharedSuggestionProvider> delegate) implements SuggestionProvider<SharedSuggestionProvider> {
      final ResourceLocation name;

      RegisteredSuggestion(ResourceLocation var1, SuggestionProvider<SharedSuggestionProvider> var2) {
         super();
         this.name = var1;
         this.delegate = var2;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<SharedSuggestionProvider> var1, SuggestionsBuilder var2) throws CommandSyntaxException {
         return this.delegate.getSuggestions(var1, var2);
      }
   }
}
