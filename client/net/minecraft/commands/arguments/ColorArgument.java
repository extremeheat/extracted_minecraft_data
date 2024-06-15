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
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.color.invalid", var0)
   );

   private ColorArgument() {
      super();
   }

   public static ColorArgument color() {
      return new ColorArgument();
   }

   public static ChatFormatting getColor(CommandContext<CommandSourceStack> var0, String var1) {
      return (ChatFormatting)var0.getArgument(var1, ChatFormatting.class);
   }

   public ChatFormatting parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      ChatFormatting var3 = ChatFormatting.getByName(var2);
      if (var3 != null && !var3.isFormat()) {
         return var3;
      } else {
         throw ERROR_INVALID_VALUE.createWithContext(var1, var2);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(ChatFormatting.getNames(true, false), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
