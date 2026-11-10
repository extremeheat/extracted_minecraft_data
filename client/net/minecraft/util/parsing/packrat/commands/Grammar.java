package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;

public record Grammar<S extends CommandParserState, T>(Dictionary<S> rules, NamedRule<S, T> top) implements CommandArgumentParser<S, T> {
   public Grammar {
      super();
      rules.checkAllBound();
   }

   public Optional<T> parse(final ParseState<S> state) {
      return state.<T>parseTopRule(this.top);
   }

   public T parseForCommands(final S reader) throws CommandSyntaxException {
      ErrorCollector.LongestOnly errorCollector = new ErrorCollector.LongestOnly();
      StringReaderParserState<S> state = new StringReaderParserState<S>(errorCollector, reader);
      Optional<T> result = this.parse(state);
      if (result.isPresent()) {
         return (T)result.get();
      } else {
         List<ErrorEntry> errorEntries = errorCollector.entries();
         List<Exception> exceptions = errorEntries.stream().mapMulti((entry, output) -> {
            Object patt0$temp = entry.reason();
            if (patt0$temp instanceof DelayedException<?> delayedException) {
               output.accept(delayedException.create(reader.reader().getString(), entry.cursor()));
            } else {
               patt0$temp = entry.reason();
               if (patt0$temp instanceof Exception exception) {
                  output.accept(exception);
               }
            }

         }).toList();

         for(Exception exception : exceptions) {
            if (exception instanceof CommandSyntaxException) {
               CommandSyntaxException cse = (CommandSyntaxException)exception;
               throw cse;
            }
         }

         if (exceptions.size() == 1) {
            Object var11 = exceptions.get(0);
            if (var11 instanceof RuntimeException) {
               RuntimeException re = (RuntimeException)var11;
               throw re;
            }
         }

         Stream var10002 = errorEntries.stream().map(ErrorEntry::toString);
         throw new IllegalStateException("Failed to parse: " + (String)var10002.collect(Collectors.joining(", ")));
      }
   }

   public CompletableFuture<Suggestions> parseForSuggestions(final CommandContext<? extends SuggestionContext> context, final CommandParserState.Factory<? extends S> stateFactory, final SuggestionsBuilder builder) throws CommandSyntaxException {
      S reader = stateFactory.create((ParseContext)context.getSource(), builder.getInput());
      reader.reader().setCursor(builder.getStart());
      ErrorCollector.LongestOnly errorCollector = new ErrorCollector.LongestOnly();
      StringReaderParserState<S> state = new StringReaderParserState<S>(errorCollector, reader);
      this.parse(state);
      List<ErrorEntry> errorEntries = errorCollector.entries();
      if (errorEntries.isEmpty()) {
         return builder.buildFuture();
      } else {
         SuggestionsBuilder offsetBuilder = builder.createOffset(errorCollector.cursor());

         for(ErrorEntry entry : errorEntries) {
            entry.suggestion().asCommandSuggestions().getSuggestions(context, offsetBuilder);
         }

         return offsetBuilder.buildFuture();
      }
   }
}
