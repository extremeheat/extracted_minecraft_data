package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
   private static final ResourceLocation DEFAULT_NAME = new ResourceLocation("ask_server");
   public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER;
   public static final SuggestionProvider<CommandSourceStack> ALL_RECIPES;
   public static final SuggestionProvider<CommandSourceStack> AVAILABLE_SOUNDS;
   public static final SuggestionProvider<CommandSourceStack> SUMMONABLE_ENTITIES;

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(ResourceLocation var0, SuggestionProvider<SharedSuggestionProvider> var1) {
      if (PROVIDERS_BY_NAME.containsKey(var0)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + var0);
      } else {
         PROVIDERS_BY_NAME.put(var0, var1);
         return new SuggestionProviders.Wrapper(var0, var1);
      }
   }

   public static SuggestionProvider<SharedSuggestionProvider> getProvider(ResourceLocation var0) {
      return (SuggestionProvider)PROVIDERS_BY_NAME.getOrDefault(var0, ASK_SERVER);
   }

   public static ResourceLocation getName(SuggestionProvider<SharedSuggestionProvider> var0) {
      return var0 instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)var0).name : DEFAULT_NAME;
   }

   public static SuggestionProvider<SharedSuggestionProvider> safelySwap(SuggestionProvider<SharedSuggestionProvider> var0) {
      return var0 instanceof SuggestionProviders.Wrapper ? var0 : ASK_SERVER;
   }

   static {
      ASK_SERVER = register(DEFAULT_NAME, (var0, var1) -> {
         return ((SharedSuggestionProvider)var0.getSource()).customSuggestion(var0, var1);
      });
      ALL_RECIPES = register(new ResourceLocation("all_recipes"), (var0, var1) -> {
         return SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)var0.getSource()).getRecipeNames(), var1);
      });
      AVAILABLE_SOUNDS = register(new ResourceLocation("available_sounds"), (var0, var1) -> {
         return SharedSuggestionProvider.suggestResource((Iterable)((SharedSuggestionProvider)var0.getSource()).getAvailableSoundEvents(), var1);
      });
      SUMMONABLE_ENTITIES = register(new ResourceLocation("summonable_entities"), (var0, var1) -> {
         return SharedSuggestionProvider.suggestResource(Registry.ENTITY_TYPE.stream().filter(EntityType::canSummon), var1, EntityType::getKey, (var0x) -> {
            return new TranslatableComponent(Util.makeDescriptionId("entity", EntityType.getKey(var0x)), new Object[0]);
         });
      });
   }

   public static class Wrapper implements SuggestionProvider<SharedSuggestionProvider> {
      private final SuggestionProvider<SharedSuggestionProvider> delegate;
      private final ResourceLocation name;

      public Wrapper(ResourceLocation var1, SuggestionProvider<SharedSuggestionProvider> var2) {
         super();
         this.delegate = var2;
         this.name = var1;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<SharedSuggestionProvider> var1, SuggestionsBuilder var2) throws CommandSyntaxException {
         return this.delegate.getSuggestions(var1, var2);
      }
   }
}
