package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.ParseState;

public record Grammar<T>(Dictionary<StringReader> rules, Atom<T> top) {
   public Grammar(Dictionary<StringReader> rules, Atom<T> top) {
      super();
      this.rules = rules;
      this.top = top;
   }

   public Optional<T> parse(ParseState<StringReader> var1) {
      return var1.parseTopRule(this.top);
   }

   public T parseForCommands(StringReader var1) throws CommandSyntaxException {
      ErrorCollector.LongestOnly var2 = new ErrorCollector.LongestOnly();
      StringReaderParserState var3 = new StringReaderParserState(this.rules(), var2, var1);
      Optional var4 = this.parse(var3);
      if (var4.isPresent()) {
         return (T)var4.get();
      } else {
         List var5 = var2.entries().stream().mapMulti((var0, var1x) -> {
            if (var0.reason() instanceof Exception var2x) {
               var1x.accept(var2x);
            }
         }).toList();

         for (Exception var7 : var5) {
            if (var7 instanceof CommandSyntaxException var8) {
               throw var8;
            }
         }

         if (var5.size() == 1 && var5.get(0) instanceof RuntimeException var9) {
            throw var9;
         } else {
            throw new IllegalStateException("Failed to parse: " + var2.entries().stream().map(ErrorEntry::toString).collect(Collectors.joining(", ")));
         }
      }
   }

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

         for (ErrorEntry var8 : var5) {
            if (var8.suggestions() instanceof ResourceSuggestion var9) {
               SharedSuggestionProvider.suggestResource(var9.possibleResources(), var6);
            } else {
               SharedSuggestionProvider.suggest(var8.suggestions().possibleValues(var4), var6);
            }
         }

         return var6.buildFuture();
      }
   }
}
