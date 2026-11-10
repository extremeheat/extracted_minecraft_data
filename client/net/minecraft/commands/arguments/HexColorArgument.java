package net.minecraft.commands.arguments;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class HexColorArgument implements SerializableArgument.ContextFree<Integer> {
   @VisibleForTesting
   public static final HexColorArgument INSTANCE = new HexColorArgument();
   public static final SerializableArgument.Type<HexColorArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_HEX;

   private HexColorArgument() {
      super();
   }

   public SerializableArgument.Type<HexColorArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Integer> hexColor() {
      return INSTANCE.toBrigadier();
   }

   public static Integer getHexColor(final CommandContext<?> context, final String name) {
      return (Integer)context.getArgument(name, Integer.class);
   }

   public Integer parse(final StringReader reader) throws CommandSyntaxException {
      String colorString = reader.readUnquotedString();
      Integer var10000;
      switch (colorString.length()) {
         case 3 -> var10000 = ARGB.color(duplicateDigit(Integer.parseInt(colorString, 0, 1, 16)), duplicateDigit(Integer.parseInt(colorString, 1, 2, 16)), duplicateDigit(Integer.parseInt(colorString, 2, 3, 16)));
         case 6 -> var10000 = ARGB.color(Integer.parseInt(colorString, 0, 2, 16), Integer.parseInt(colorString, 2, 4, 16), Integer.parseInt(colorString, 4, 6, 16));
         default -> throw ERROR_INVALID_HEX.createWithContext(reader, colorString);
      }

      return var10000;
   }

   private static int duplicateDigit(final int digit) {
      return digit * 17;
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(EXAMPLES, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<HexColorArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("F00", "FF0000");
      ERROR_INVALID_HEX = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.hexcolor.invalid", value));
   }
}
