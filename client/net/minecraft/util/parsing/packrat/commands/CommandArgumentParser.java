package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
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
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.HolderGetter;

public interface CommandArgumentParser<S extends CommandParserState, T> {
   T parseForCommands(S reader) throws CommandSyntaxException;

   CompletableFuture<Suggestions> parseForSuggestions(CommandContext<? extends SuggestionContext> context, CommandParserState.Factory<? extends S> stateFactory, SuggestionsBuilder builder) throws CommandSyntaxException;

   default <U> CommandArgumentParser<S, U> mapResult(final Function<T, U> mapper) {
      return new CommandArgumentParser<S, U>() {
         {
            Objects.requireNonNull(CommandArgumentParser.this);
         }

         public U parseForCommands(final S reader) throws CommandSyntaxException {
            return (U)mapper.apply(CommandArgumentParser.this.parseForCommands(reader));
         }

         public CompletableFuture<Suggestions> parseForSuggestions(final CommandContext<? extends SuggestionContext> context, final CommandParserState.Factory<? extends S> stateFactory, final SuggestionsBuilder builder) throws CommandSyntaxException {
            return CommandArgumentParser.this.parseForSuggestions(context, stateFactory, builder);
         }
      };
   }

   static <S extends RegistryAwareState, T, O> CommandArgumentParser<S, T> withCodec(final DynamicOps<O> ops, final CommandArgumentParser<? super S, O> valueParser, final Codec<T> codec, final DynamicCommandExceptionType exceptionType) {
      return new CommandArgumentParser<S, T>() {
         public T parseForCommands(final S state) throws CommandSyntaxException {
            StringReader reader = state.reader();
            int cursor = reader.getCursor();
            O tag = valueParser.parseForCommands(state);
            DataResult<T> result = codec.parse(state.registries().createSerializationContext(ops), tag);
            return (T)result.getOrThrow((message) -> {
               reader.setCursor(cursor);
               return exceptionType.createWithContext(reader, message);
            });
         }

         public CompletableFuture<Suggestions> parseForSuggestions(final CommandContext<? extends SuggestionContext> context, final CommandParserState.Factory<? extends S> stateFactory, final SuggestionsBuilder builder) throws CommandSyntaxException {
            return valueParser.parseForSuggestions(context, stateFactory, builder);
         }
      };
   }

   public interface RegistryAwareState extends CommandParserState {
      CommandParserState.Factory<RegistryAwareState> FACTORY = (context, reader) -> new Simple(reader, context.holderGetters());

      HolderGetter.Provider registries();

      public static record Simple(StringReader reader, HolderGetter.Provider registries) implements RegistryAwareState {
         public Simple {
            super();
         }
      }
   }
}
