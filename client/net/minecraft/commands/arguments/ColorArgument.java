package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ColorArgument implements ArgumentType<ChatFormatting> {
   private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.color.invalid", value));

   private ColorArgument() {
      super();
   }

   public static ColorArgument color() {
      return new ColorArgument();
   }

   public static ChatFormatting getColor(final CommandContext<CommandSourceStack> context, final String name) {
      return (ChatFormatting)context.getArgument(name, ChatFormatting.class);
   }

   public ChatFormatting parse(final StringReader reader) throws CommandSyntaxException {
      String id = reader.readUnquotedString();
      ChatFormatting result = ChatFormatting.getByName(id);
      if (result != null && !result.isFormat()) {
         return result;
      } else {
         throw ERROR_INVALID_VALUE.createWithContext(reader, id);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> contextBuilder, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(ChatFormatting.getNames(true, false), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
