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
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
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
import net.minecraft.tags.TagKey;
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

   Stream<ResourceLocation> getRecipeNames();

   CompletableFuture<Suggestions> customSuggestion(CommandContext<?> var1);

   default Collection<TextCoordinates> getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection<TextCoordinates> getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   Set<ResourceKey<Level>> levels();

   RegistryAccess registryAccess();

   FeatureFlagSet enabledFeatures();

   default void suggestRegistryElements(Registry<?> var1, ElementSuggestionType var2, SuggestionsBuilder var3) {
      if (var2.shouldSuggestTags()) {
         suggestResource(var1.getTagNames().map(TagKey::location), var3, "#");
      }

      if (var2.shouldSuggestElements()) {
         suggestResource((Iterable)var1.keySet(), var3);
      }

   }

   CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> var1, ElementSuggestionType var2, SuggestionsBuilder var3, CommandContext<?> var4);

   boolean hasPermission(int var1);

   static <T> void filterResources(Iterable<T> var0, String var1, Function<T, ResourceLocation> var2, Consumer<T> var3) {
      boolean var4 = var1.indexOf(58) > -1;
      Iterator var5 = var0.iterator();

      while(true) {
         while(var5.hasNext()) {
            Object var6 = var5.next();
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

         return;
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
      filterResources(var0, var3, var2, (var0x) -> {
         return var0x;
      }, (var2x) -> {
         var1.suggest(var2 + String.valueOf(var2x));
      });
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> var0, SuggestionsBuilder var1, String var2) {
      Objects.requireNonNull(var0);
      return suggestResource(var0::iterator, var1, var2);
   }

   static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(var0, var2, (var0x) -> {
         return var0x;
      }, (var1x) -> {
         var1.suggest(var1x.toString());
      });
      return var1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggestResource(Iterable<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3) {
      String var4 = var1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(var0, var4, var2, (var3x) -> {
         var1.suggest(((ResourceLocation)var2.apply(var3x)).toString(), (Message)var3.apply(var3x));
      });
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> var0, SuggestionsBuilder var1) {
      Objects.requireNonNull(var0);
      return suggestResource(var0::iterator, var1);
   }

   static <T> CompletableFuture<Suggestions> suggestResource(Stream<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3) {
      Objects.requireNonNull(var0);
      return suggestResource(var0::iterator, var1, var2, var3);
   }

   static CompletableFuture<Suggestions> suggestCoordinates(String var0, Collection<TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            TextCoordinates var6 = (TextCoordinates)var5.next();
            String var7 = var6.x + " " + var6.y + " " + var6.z;
            if (var3.test(var7)) {
               var4.add(var6.x);
               var4.add(var6.x + " " + var6.y);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         String var8;
         Iterator var10;
         TextCoordinates var11;
         if (var9.length == 1) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (TextCoordinates)var10.next();
               var8 = var9[0] + " " + var11.y + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var9[0] + " " + var11.y);
                  var4.add(var8);
               }
            }
         } else if (var9.length == 2) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (TextCoordinates)var10.next();
               var8 = var9[0] + " " + var9[1] + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest((Iterable)var4, var2);
   }

   static CompletableFuture<Suggestions> suggest2DCoordinates(String var0, Collection<TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            TextCoordinates var6 = (TextCoordinates)var5.next();
            String var7 = var6.x + " " + var6.z;
            if (var3.test(var7)) {
               var4.add(var6.x);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         if (var9.length == 1) {
            Iterator var10 = var1.iterator();

            while(var10.hasNext()) {
               TextCoordinates var11 = (TextCoordinates)var10.next();
               String var8 = var9[0] + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest((Iterable)var4, var2);
   }

   static CompletableFuture<Suggestions> suggest(Iterable<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (matchesSubStr(var2, var4.toLowerCase(Locale.ROOT))) {
            var1.suggest(var4);
         }
      }

      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Stream var10000 = var0.filter((var1x) -> {
         return matchesSubStr(var2, var1x.toLowerCase(Locale.ROOT));
      });
      Objects.requireNonNull(var1);
      var10000.forEach(var1::suggest);
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      String[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (matchesSubStr(var2, var6.toLowerCase(Locale.ROOT))) {
            var1.suggest(var6);
         }
      }

      return var1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggest(Iterable<T> var0, SuggestionsBuilder var1, Function<T, String> var2, Function<T, Message> var3) {
      String var4 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var5 = var0.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         String var7 = (String)var2.apply(var6);
         if (matchesSubStr(var4, var7.toLowerCase(Locale.ROOT))) {
            var1.suggest(var7, (Message)var3.apply(var6));
         }
      }

      return var1.buildFuture();
   }

   static boolean matchesSubStr(String var0, String var1) {
      for(int var2 = 0; !var1.startsWith(var0, var2); ++var2) {
         int var3 = var1.indexOf(46, var2);
         int var4 = var1.indexOf(95, var2);
         if (Math.max(var3, var4) < 0) {
            return false;
         }

         if (var3 >= 0 && var4 >= 0) {
            var2 = Math.min(var4, var3);
         } else {
            var2 = var3 >= 0 ? var3 : var4;
         }
      }

      return true;
   }

   public static class TextCoordinates {
      public static final TextCoordinates DEFAULT_LOCAL = new TextCoordinates("^", "^", "^");
      public static final TextCoordinates DEFAULT_GLOBAL = new TextCoordinates("~", "~", "~");
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

      // $FF: synthetic method
      private static ElementSuggestionType[] $values() {
         return new ElementSuggestionType[]{TAGS, ELEMENTS, ALL};
      }
   }
}
