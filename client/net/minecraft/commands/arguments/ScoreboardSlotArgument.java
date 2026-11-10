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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;

public class ScoreboardSlotArgument implements SerializableArgument.ContextFree<DisplaySlot> {
   private static final ScoreboardSlotArgument INSTANCE = new ScoreboardSlotArgument();
   public static final SerializableArgument.Type<ScoreboardSlotArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   private ScoreboardSlotArgument() {
      super();
   }

   public SerializableArgument.Type<ScoreboardSlotArgument> type() {
      return TYPE;
   }

   public static ArgumentType<DisplaySlot> displaySlot() {
      return INSTANCE.toBrigadier();
   }

   public static DisplaySlot getDisplaySlot(final CommandContext<?> context, final String name) {
      return (DisplaySlot)context.getArgument(name, DisplaySlot.class);
   }

   public DisplaySlot parse(final StringReader reader) throws CommandSyntaxException {
      String name = reader.readUnquotedString();
      DisplaySlot result = DisplaySlot.CODEC.byName(name);
      if (result == null) {
         throw ERROR_INVALID_VALUE.createWithContext(reader, name);
      } else {
         return result;
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(Arrays.stream(DisplaySlot.values()).map(DisplaySlot::getSerializedName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ScoreboardSlotArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("sidebar", "foo.bar");
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.scoreboardDisplaySlot.invalid", value));
   }
}
