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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class HexColorArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("F00", "FF0000");
   public static final DynamicCommandExceptionType ERROR_INVALID_HEX = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.hexcolor.invalid", var0));

   private HexColorArgument() {
      super();
   }

   public static HexColorArgument hexColor() {
      return new HexColorArgument();
   }

   public static Integer getHexColor(CommandContext<CommandSourceStack> var0, String var1) {
      return (Integer)var0.getArgument(var1, Integer.class);
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      Integer var10000;
      switch (var2.length()) {
         case 3 -> var10000 = ARGB.color(duplicateDigit(Integer.parseInt(var2, 0, 1, 16)), duplicateDigit(Integer.parseInt(var2, 1, 2, 16)), duplicateDigit(Integer.parseInt(var2, 2, 3, 16)));
         case 6 -> var10000 = ARGB.color(Integer.parseInt(var2, 0, 2, 16), Integer.parseInt(var2, 2, 4, 16), Integer.parseInt(var2, 4, 6, 16));
         default -> throw ERROR_INVALID_HEX.createWithContext(var1, var2);
      }

      return var10000;
   }

   private static int duplicateDigit(int var0) {
      return var0 * 17;
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(EXAMPLES, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
