package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public record Grammar<T>(Dictionary<StringReader> rules, Atom<T> top) {
   public Grammar(Dictionary<StringReader> var1, Atom<T> var2) {
      super();
      this.rules = var1;
      this.top = var2;
   }

   public Optional<T> parse(ParseState<StringReader> var1) {
      return var1.parseTopRule(this.top);
   }

   public T parseForCommands(StringReader var1) throws CommandSyntaxException {
      ErrorCollector.LongestOnly var2 = new ErrorCollector.LongestOnly();
      StringReaderParserState var3 = new StringReaderParserState(this.rules(), var2, var1);
      Optional var4 = this.parse(var3);
      if (var4.isPresent()) {
         return var4.get();
      } else {
         List var5 = var2.entries().stream().mapMulti((var0, var1x) -> {
            Object var3 = var0.reason();
            if (var3 instanceof Exception var2) {
               var1x.accept(var2);
            }

         }).toList();
         Iterator var6 = var5.iterator();

         Exception var7;
         do {
            if (!var6.hasNext()) {
               if (var5.size() == 1) {
                  Object var10 = var5.get(0);
                  if (var10 instanceof RuntimeException) {
                     RuntimeException var9 = (RuntimeException)var10;
                     throw var9;
                  }
               }

               Stream var10002 = var2.entries().stream().map(ErrorEntry::toString);
               throw new IllegalStateException("Failed to parse: " + (String)var10002.collect(Collectors.joining(", ")));
            }

            var7 = (Exception)var6.next();
         } while(!(var7 instanceof CommandSyntaxException));

         CommandSyntaxException var8 = (CommandSyntaxException)var7;
         throw var8;
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
         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            ErrorEntry var8 = (ErrorEntry)var7.next();
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

   public Dictionary<StringReader> rules() {
      return this.rules;
   }

   public Atom<T> top() {
      return this.top;
   }
}
