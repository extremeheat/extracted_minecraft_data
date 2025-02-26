package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface CommandArgumentParser<T> {
   T parseForCommands(StringReader var1) throws CommandSyntaxException;

   CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder var1);

   default <S> CommandArgumentParser<S> mapResult(final Function<T, S> var1) {
      return new CommandArgumentParser<S>() {
         public S parseForCommands(StringReader var1x) throws CommandSyntaxException {
            return (S)var1.apply(CommandArgumentParser.this.parseForCommands(var1x));
         }

         public CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder var1x) {
            return CommandArgumentParser.this.parseForSuggestions(var1x);
         }
      };
   }

   default <T, O> CommandArgumentParser<T> withCodec(final DynamicOps<O> var1, final CommandArgumentParser<O> var2, final Codec<T> var3, final DynamicCommandExceptionType var4) {
      return new CommandArgumentParser<T>() {
         public T parseForCommands(StringReader var1x) throws CommandSyntaxException {
            int var2x = var1x.getCursor();
            Object var3x = var2.parseForCommands(var1x);
            DataResult var4x = var3.parse(var1, var3x);
            return (T)var4x.getOrThrow((var3xx) -> {
               var1x.setCursor(var2x);
               return var4.createWithContext(var1x, var3xx);
            });
         }

         public CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder var1x) {
            return CommandArgumentParser.this.parseForSuggestions(var1x);
         }
      };
   }
}
