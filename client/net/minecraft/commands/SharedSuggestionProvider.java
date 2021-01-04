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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public interface SharedSuggestionProvider {
   Collection<String> getOnlinePlayerNames();

   default Collection<String> getSelectedEntities() {
      return Collections.emptyList();
   }

   Collection<String> getAllTeams();

   Collection<ResourceLocation> getAvailableSoundEvents();

   Stream<ResourceLocation> getRecipeNames();

   CompletableFuture<Suggestions> customSuggestion(CommandContext<SharedSuggestionProvider> var1, SuggestionsBuilder var2);

   default Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

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
               if (var8.startsWith(var1)) {
                  var3.accept(var6);
               }
            } else if (var7.getNamespace().startsWith(var1) || var7.getNamespace().equals("minecraft") && var7.getPath().startsWith(var1)) {
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
         var1.suggest(var2 + var2x);
      });
      return var1.buildFuture();
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
      return suggestResource(var0::iterator, var1);
   }

   static <T> CompletableFuture<Suggestions> suggestResource(Stream<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3) {
      return suggestResource(var0::iterator, var1, var2, var3);
   }

   static CompletableFuture<Suggestions> suggestCoordinates(String var0, Collection<SharedSuggestionProvider.TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            SharedSuggestionProvider.TextCoordinates var6 = (SharedSuggestionProvider.TextCoordinates)var5.next();
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
         SharedSuggestionProvider.TextCoordinates var11;
         if (var9.length == 1) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (SharedSuggestionProvider.TextCoordinates)var10.next();
               var8 = var9[0] + " " + var11.y + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var9[0] + " " + var11.y);
                  var4.add(var8);
               }
            }
         } else if (var9.length == 2) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (SharedSuggestionProvider.TextCoordinates)var10.next();
               var8 = var9[0] + " " + var9[1] + " " + var11.z;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest((Iterable)var4, var2);
   }

   static CompletableFuture<Suggestions> suggest2DCoordinates(String var0, Collection<SharedSuggestionProvider.TextCoordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            SharedSuggestionProvider.TextCoordinates var6 = (SharedSuggestionProvider.TextCoordinates)var5.next();
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
               SharedSuggestionProvider.TextCoordinates var11 = (SharedSuggestionProvider.TextCoordinates)var10.next();
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
         if (var4.toLowerCase(Locale.ROOT).startsWith(var2)) {
            var1.suggest(var4);
         }
      }

      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      var0.filter((var1x) -> {
         return var1x.toLowerCase(Locale.ROOT).startsWith(var2);
      }).forEach(var1::suggest);
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      String[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var6.toLowerCase(Locale.ROOT).startsWith(var2)) {
            var1.suggest(var6);
         }
      }

      return var1.buildFuture();
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
