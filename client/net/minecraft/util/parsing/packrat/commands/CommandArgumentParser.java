package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface CommandArgumentParser<T> {
   T parseForCommands(StringReader reader) throws CommandSyntaxException;

   CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder suggestionsBuilder);

   default <S> CommandArgumentParser<S> mapResult(final Function<T, S> mapper) {
      return new CommandArgumentParser<S>() {
         {
            Objects.requireNonNull(CommandArgumentParser.this);
         }

         public S parseForCommands(final StringReader reader) throws CommandSyntaxException {
            return (S)mapper.apply(CommandArgumentParser.this.parseForCommands(reader));
         }

         public CompletableFuture<Suggestions> parseForSuggestions(final SuggestionsBuilder suggestionsBuilder) {
            return CommandArgumentParser.this.parseForSuggestions(suggestionsBuilder);
         }
      };
   }

   default <T, O> CommandArgumentParser<T> withCodec(final DynamicOps<O> ops, final CommandArgumentParser<O> valueParser, final Codec<T> codec, final DynamicCommandExceptionType exceptionType) {
      return new CommandArgumentParser<T>() {
         {
            Objects.requireNonNull(CommandArgumentParser.this);
         }

         public T parseForCommands(final StringReader reader) throws CommandSyntaxException {
            int cursor = reader.getCursor();
            O tag = valueParser.parseForCommands(reader);
            DataResult<T> result = codec.parse(ops, tag);
            return (T)result.getOrThrow((message) -> {
               reader.setCursor(cursor);
               return exceptionType.createWithContext(reader, message);
            });
         }

         public CompletableFuture<Suggestions> parseForSuggestions(final SuggestionsBuilder suggestionsBuilder) {
            return CommandArgumentParser.this.parseForSuggestions(suggestionsBuilder);
         }
      };
   }
}
