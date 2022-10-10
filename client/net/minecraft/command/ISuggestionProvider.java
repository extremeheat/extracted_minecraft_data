package net.minecraft.command;

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
import net.minecraft.util.ResourceLocation;

public interface ISuggestionProvider {
   Collection<String> func_197011_j();

   default Collection<String> func_211270_p() {
      return Collections.emptyList();
   }

   Collection<String> func_197012_k();

   Collection<ResourceLocation> func_197010_l();

   Collection<ResourceLocation> func_199612_m();

   CompletableFuture<Suggestions> func_197009_a(CommandContext<ISuggestionProvider> var1, SuggestionsBuilder var2);

   Collection<ISuggestionProvider.Coordinates> func_199613_a(boolean var1);

   boolean func_197034_c(int var1);

   static <T> void func_210512_a(Iterable<T> var0, String var1, Function<T, ResourceLocation> var2, Consumer<T> var3) {
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
            } else if (var7.func_110624_b().startsWith(var1) || var7.func_110624_b().equals("minecraft") && var7.func_110623_a().startsWith(var1)) {
               var3.accept(var6);
            }
         }

         return;
      }
   }

   static <T> void func_210511_a(Iterable<T> var0, String var1, String var2, Function<T, ResourceLocation> var3, Consumer<T> var4) {
      if (var1.isEmpty()) {
         var0.forEach(var4);
      } else {
         String var5 = Strings.commonPrefix(var1, var2);
         if (!var5.isEmpty()) {
            String var6 = var1.substring(var5.length());
            func_210512_a(var0, var6, var3, var4);
         }
      }

   }

   static CompletableFuture<Suggestions> func_197006_a(Iterable<ResourceLocation> var0, SuggestionsBuilder var1, String var2) {
      String var3 = var1.getRemaining().toLowerCase(Locale.ROOT);
      func_210511_a(var0, var3, var2, (var0x) -> {
         return var0x;
      }, (var2x) -> {
         var1.suggest(var2 + var2x);
      });
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> func_197014_a(Iterable<ResourceLocation> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(var0, var2, (var0x) -> {
         return var0x;
      }, (var1x) -> {
         var1.suggest(var1x.toString());
      });
      return var1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> func_210514_a(Iterable<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3) {
      String var4 = var1.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(var0, var4, var2, (var3x) -> {
         var1.suggest(((ResourceLocation)var2.apply(var3x)).toString(), (Message)var3.apply(var3x));
      });
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> func_212476_a(Stream<ResourceLocation> var0, SuggestionsBuilder var1) {
      return func_197014_a(var0::iterator, var1);
   }

   static <T> CompletableFuture<Suggestions> func_201725_a(Stream<T> var0, SuggestionsBuilder var1, Function<T, ResourceLocation> var2, Function<T, Message> var3) {
      return func_210514_a(var0::iterator, var1, var2, var3);
   }

   static CompletableFuture<Suggestions> func_209000_a(String var0, Collection<ISuggestionProvider.Coordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            ISuggestionProvider.Coordinates var6 = (ISuggestionProvider.Coordinates)var5.next();
            String var7 = var6.field_209006_c + " " + var6.field_209007_d + " " + var6.field_209008_e;
            if (var3.test(var7)) {
               var4.add(var6.field_209006_c);
               var4.add(var6.field_209006_c + " " + var6.field_209007_d);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         String var8;
         Iterator var10;
         ISuggestionProvider.Coordinates var11;
         if (var9.length == 1) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (ISuggestionProvider.Coordinates)var10.next();
               var8 = var9[0] + " " + var11.field_209007_d + " " + var11.field_209008_e;
               if (var3.test(var8)) {
                  var4.add(var9[0] + " " + var11.field_209007_d);
                  var4.add(var8);
               }
            }
         } else if (var9.length == 2) {
            var10 = var1.iterator();

            while(var10.hasNext()) {
               var11 = (ISuggestionProvider.Coordinates)var10.next();
               var8 = var9[0] + " " + var9[1] + " " + var11.field_209008_e;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return func_197005_b(var4, var2);
   }

   static CompletableFuture<Suggestions> func_211269_a(String var0, Collection<ISuggestionProvider.Coordinates> var1, SuggestionsBuilder var2, Predicate<String> var3) {
      ArrayList var4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty(var0)) {
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            ISuggestionProvider.Coordinates var6 = (ISuggestionProvider.Coordinates)var5.next();
            String var7 = var6.field_209006_c + " " + var6.field_209008_e;
            if (var3.test(var7)) {
               var4.add(var6.field_209006_c);
               var4.add(var7);
            }
         }
      } else {
         String[] var9 = var0.split(" ");
         if (var9.length == 1) {
            Iterator var10 = var1.iterator();

            while(var10.hasNext()) {
               ISuggestionProvider.Coordinates var11 = (ISuggestionProvider.Coordinates)var10.next();
               String var8 = var9[0] + " " + var11.field_209008_e;
               if (var3.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return func_197005_b(var4, var2);
   }

   static CompletableFuture<Suggestions> func_197005_b(Iterable<String> var0, SuggestionsBuilder var1) {
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

   static CompletableFuture<Suggestions> func_197013_a(Stream<String> var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      var0.filter((var1x) -> {
         return var1x.toLowerCase(Locale.ROOT).startsWith(var2);
      }).forEach(var1::suggest);
      return var1.buildFuture();
   }

   static CompletableFuture<Suggestions> func_197008_a(String[] var0, SuggestionsBuilder var1) {
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

   public static class Coordinates {
      public static final ISuggestionProvider.Coordinates field_209004_a = new ISuggestionProvider.Coordinates("^", "^", "^");
      public static final ISuggestionProvider.Coordinates field_209005_b = new ISuggestionProvider.Coordinates("~", "~", "~");
      public final String field_209006_c;
      public final String field_209007_d;
      public final String field_209008_e;

      public Coordinates(String var1, String var2, String var3) {
         super();
         this.field_209006_c = var1;
         this.field_209007_d = var2;
         this.field_209008_e = var3;
      }
   }
}
