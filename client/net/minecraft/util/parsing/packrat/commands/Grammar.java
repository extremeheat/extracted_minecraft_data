package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public record Grammar<T>(Dictionary<StringReader> rules, NamedRule<StringReader, T> top) implements CommandArgumentParser<T> {
   public Grammar(Dictionary<StringReader> var1, NamedRule<StringReader, T> var2) {
      super();
      var1.checkAllBound();
      this.rules = var1;
      this.top = var2;
   }

   public Optional<T> parse(ParseState<StringReader> var1) {
      return var1.<T>parseTopRule(this.top);
   }

   public T parseForCommands(StringReader var1) throws CommandSyntaxException {
      ErrorCollector.LongestOnly var2 = new ErrorCollector.LongestOnly();
      StringReaderParserState var3 = new StringReaderParserState(var2, var1);
      Optional var4 = this.parse(var3);
      if (var4.isPresent()) {
         return (T)var4.get();
      } else {
         List var5 = var2.entries();
         List var6 = var5.stream().mapMulti((var1x, var2x) -> {
            Object var5 = var1x.reason();
            if (var5 instanceof DelayedException var3) {
               var2x.accept(var3.create(var1.getString(), var1x.cursor()));
            } else {
               var5 = var1x.reason();
               if (var5 instanceof Exception var4) {
                  var2x.accept(var4);
               }
            }

         }).toList();

         for(Exception var8 : var6) {
            if (var8 instanceof CommandSyntaxException) {
               CommandSyntaxException var9 = (CommandSyntaxException)var8;
               throw var9;
            }
         }

         if (var6.size() == 1) {
            Object var11 = var6.get(0);
            if (var11 instanceof RuntimeException) {
               RuntimeException var10 = (RuntimeException)var11;
               throw var10;
            }
         }

         Stream var10002 = var5.stream().map(ErrorEntry::toString);
         throw new IllegalStateException("Failed to parse: " + (String)var10002.collect(Collectors.joining(", ")));
      }
   }

   public CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder var1) {
      StringReader var2 = new StringReader(var1.getInput());
      var2.setCursor(var1.getStart());
      ErrorCollector.LongestOnly var3 = new ErrorCollector.LongestOnly();
      StringReaderParserState var4 = new StringReaderParserState(var3, var2);
      this.parse(var4);
      List var5 = var3.entries();
      if (var5.isEmpty()) {
         return var1.buildFuture();
      } else {
         SuggestionsBuilder var6 = var1.createOffset(var3.cursor());

         for(ErrorEntry var8 : var5) {
            SuggestionSupplier var10 = var8.suggestions();
            if (var10 instanceof ResourceSuggestion) {
               ResourceSuggestion var9 = (ResourceSuggestion)var10;
               SharedSuggestionProvider.suggestResource(var9.possibleResources(), var6);
            } else {
               SharedSuggestionProvider.suggest(var8.suggestions().possibleValues(var4), var6);
            }
         }

         return var6.buildFuture();
      }
   }
}
