package net.minecraft.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;

public interface SharedSuggestionProvider {
   Collection<String> getOnlinePlayerNames();

   default Collection<String> getCustomTabSugggestions() {
      return this.getOnlinePlayerNames();
   }

   default Collection<String> getSelectedEntities() {
      return Collections.emptyList();
   }

   Collection<String> getAllTeams();

   Stream<ResourceLocation> getAvailableSounds();

   CompletableFuture<Suggestions> customSuggestion(CommandContext<?> var1);

   default Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   Set<ResourceKey<Level>> levels();

   RegistryAccess registryAccess();

   FeatureFlagSet enabledFeatures();

   default void suggestRegistryElements(Registry<?> var1, SharedSuggestionProvider.ElementSuggestionType var2, SuggestionsBuilder var3) {
      if (var2.shouldSuggestTags()) {
         suggestResource(var1.getTags().map(var0 -> var0.key().location()), var3, "#");
      }

      if (var2.shouldSuggestElements()) {
         suggestResource(var1.keySet(), var3);
      }
   }

   CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<?>> var1, SharedSuggestionProvider.ElementSuggestionType var2, SuggestionsBuilder var3, CommandContext<?> var4
   );

   boolean hasPermission(int var1);

   static <T> void filterResources(Iterable<T> var0, String var1, Function<T, ResourceLocation> var2, Consumer<T> var3) {
      boolean var4 = var1.indexOf(58) > -1;

      for (Object var6 : var0) {
         ResourceLocation var7 = (ResourceLocation)var2.apply(var6);
         if (var4) {
            String var8 = var7.toString();
            if (matchesSubStr(var1, var8)) {
               var3.accept(var6);
            }
         } else if (matchesSubStr(var1, var7.getNamespace()) || var7.getNamespace().equals("minecraft") && matchesSubStr(var1, var7.getPath())) {
            var3.accept(var6);
         }
      }
   }

   static <T> void filterResources(Iterable<T> var0, String var1, String var2, Function<T, ResourceLocation> var3, Consumer<T> var4) {
      if (var1.isEmpty()) {
         var0.forEach(var4);
      } else {
         String var5 = Strings.commonPrefix(var1, var2);
         if (!var5.isEmpty()) {
            String var6 = var1.substring(var5.length());
            filterResources(var0, var6, var3, var4);
         }
      }
   }

   static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> var0, SuggestionsBuilder var1, String var2) {
      String var3 = var1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(var0, var3, var2, var0x -> var0x, var2x -> var1.suggest(var2 + var2x));
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> var0, SuggestionsBuilder var1, String var2) {
      return suggestResource(var0::iterator, var1, var2);
   }

   static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(var0, var2, var0x -> var0x, var1x -> var1.suggest(var1x.toString()));
      return var1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggestResource(
      Iterable<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3
   ) {
      String var4 = var1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(var0, var4, var2, var3x -> var1.suggest(((ResourceLocation)var2.apply(var3x)).toString(), (Message)var3.apply(var3x)));
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> var0, SuggestionsBuilder var1) {
      return suggestResource(var0::iterator, var1);
   }

   static <T> CompletableFuture<Suggestions> suggestResource(
      Stream<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3
   ) {
      return suggestResource(var0::iterator, var1, var2, var3);
   }

   static CompletableFuture<Suggestions> suggestCoordinates(
      String var0, Collection<SharedSuggestionProvider.TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3
   ) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         for (SharedSuggestionProvider.TextCoordinates var6 : var1) {
            String var7 = var6.x + " " + var6.y + " " + var6.z;
            if (var3.test(var7)) {
               var4.add(var6.x);
               var4.add(var6.x + " " + var6.y);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         if (var9.length == 1) {
            for (SharedSuggestionProvider.TextCoordinates var12 : var1) {
               String var8 = var9[0] + " " + var12.y + " " + var12.z;
               if (var3.test(var8)) {
                  var4.add(var9[0] + " " + var12.y);
                  var4.add(var8);
               }
            }
         } else if (var9.length == 2) {
            for (SharedSuggestionProvider.TextCoordinates var13 : var1) {
               String var14 = var9[0] + " " + var9[1] + " " + var13.z;
               if (var3.test(var14)) {
                  var4.add(var14);
               }
            }
         }
      }

      return suggest(var4, var2);
   }

   static CompletableFuture<Suggestions> suggest2DCoordinates(
      String var0, Collection<SharedSuggestionProvider.TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3
   ) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         for (SharedSuggestionProvider.TextCoordinates var6 : var1) {
            String var7 = var6.x + " " + var6.z;
            if (var3.test(var7)) {
               var4.add(var6.x);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         if (var9.length == 1) {
            for (SharedSuggestionProvider.TextCoordinates var11 : var1) {
               String var8 = var9[0] + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest(var4, var2);
   }

   static CompletableFuture<Suggestions> suggest(Iterable<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);

      for (String var4 : var0) {
         if (matchesSubStr(var2, var4.toLowerCase(Locale.ROOT))) {
            var1.suggest(var4);
         }
      }

      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      var0.filter(var1x -> matchesSubStr(var2, var1x.toLowerCase(Locale.ROOT))).forEach(var1::suggest);
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);

      for (String var6 : var0) {
         if (matchesSubStr(var2, var6.toLowerCase(Locale.ROOT))) {
            var1.suggest(var6);
         }
      }

      return var1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggest(Iterable<T> var0, SuggestionsBuilder var1, Function<T, String> var2, Function<T, Message> var3) {
      String var4 = var1.getRemaining().toLowerCase(Locale.ROOT);

      for (Object var6 : var0) {
         String var7 = (String)var2.apply(var6);
         if (matchesSubStr(var4, var7.toLowerCase(Locale.ROOT))) {
            var1.suggest(var7, (Message)var3.apply(var6));
         }
      }

      return var1.buildFuture();
   }

   static boolean matchesSubStr(String var0, String var1) {
      for (int var5 = 0; !var1.startsWith(var0, var5); var5++) {
         int var3 = var1.indexOf(46, var5);
         int var4 = var1.indexOf(95, var5);
         if (Math.max(var3, var4) < 0) {
            return false;
         }

         if (var3 >= 0 && var4 >= 0) {
            var5 = Math.min(var4, var3);
         } else {
            var5 = var3 >= 0 ? var3 : var4;
         }
      }

      return true;
   }

   public static enum ElementSuggestionType {
      TAGS,
      ELEMENTS,
      ALL;

      private ElementSuggestionType() {
      }

      public boolean shouldSuggestTags() {
         return this == TAGS || this == ALL;
      }

      public boolean shouldSuggestElements() {
         return this == ELEMENTS || this == ALL;
      }
   }

   public static class TextCoordinates {
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_LOCAL = new SharedSuggestionProvider.TextCoordinates("^", "^", "^");
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_GLOBAL = new SharedSuggestionProvider.TextCoordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public TextCoordinates(String var1, String var2, String var3) {
         super();
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }
   }
}
