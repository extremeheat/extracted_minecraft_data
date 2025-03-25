package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;

public abstract class ParserBasedArgument<T> implements ArgumentType<T> {
   private final CommandArgumentParser<T> parser;

   public ParserBasedArgument(CommandArgumentParser<T> var1) {
      super();
      this.parser = var1;
   }

   public T parse(StringReader var1) throws CommandSyntaxException {
      return this.parser.parseForCommands(var1);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.parser.parseForSuggestions(var2);
   }
}
