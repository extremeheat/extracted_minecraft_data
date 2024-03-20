package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public record Grammar<T>(Dictionary<StringReader> a, Atom<T> b) {
   private final Dictionary<StringReader> rules;
   private final Atom<T> top;

   public Grammar(Dictionary<StringReader> var1, Atom<T> var2) {
      super();
      this.rules = var1;
      this.top = var2;
   }

   public Optional<T> parse(ParseState<StringReader> var1) {
      return var1.parseTopRule(this.top);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public T parseForCommands(StringReader var1) throws CommandSyntaxException {
      ErrorCollector.LongestOnly var2 = new ErrorCollector.LongestOnly();
      StringReaderParserState var3 = new StringReaderParserState(this.rules(), var2, var1);
      Optional var4 = this.parse(var3);
      if (var4.isPresent()) {
         return (T)var4.get();
      } else {
         List var5 = var2.entries().stream().mapMulti((var0, var1x) -> {
            Object var3xx = var0.reason();
            if (var3xx instanceof Exception var2xx) {
               var1x.accept(var2xx);
            }
         }).toList();

         for(Exception var7 : var5) {
            if (var7 instanceof CommandSyntaxException var8) {
               throw var8;
            }
         }

         if (var5.size() == 1) {
            Object var10 = var5.get(0);
            if (var10 instanceof RuntimeException var9) {
               throw var9;
            }
         }

         throw new IllegalStateException("Failed to parse: " + (String)var2.entries().stream().map(ErrorEntry::toString).collect(Collectors.joining(", ")));
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder var1) {
      StringReader var2 = new StringReader(var1.getInput());
      var2.setCursor(var1.getStart());
      ErrorCollector.LongestOnly var3 = new ErrorCollector.LongestOnly();
      StringReaderParserState var4 = new StringReaderParserState(this.rules(), var3, var2);
      this.parse(var4);
      List var5 = var3.entries();
      if (var5.isEmpty()) {
         return var1.buildFuture();
      } else {
         SuggestionsBuilder var6 = var1.createOffset(var3.cursor());

         for(ErrorEntry var8 : var5) {
            SuggestionSupplier var10 = var8.suggestions();
            if (var10 instanceof ResourceSuggestion var9) {
               SharedSuggestionProvider.suggestResource(var9.possibleResources(), var6);
            } else {
               SharedSuggestionProvider.suggest(var8.suggestions().possibleValues(var4), var6);
            }
         }

         return var6.buildFuture();
      }
   }
}
