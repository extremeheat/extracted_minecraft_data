package net.minecraft.commands.arguments;

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
import net.minecraft.world.scores.TeamColor;

public class TeamColorArgument implements SerializableArgument.ContextFree<TeamColor> {
   private static final TeamColorArgument INSTANCE = new TeamColorArgument();
   public static final SerializableArgument.Type<TeamColorArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   private TeamColorArgument() {
      super();
   }

   public SerializableArgument.Type<TeamColorArgument> type() {
      return TYPE;
   }

   public static ArgumentType<TeamColor> teamColor() {
      return INSTANCE.toBrigadier();
   }

   public static TeamColor getTeamColor(final CommandContext<?> context, final String name) {
      return (TeamColor)context.getArgument(name, TeamColor.class);
   }

   public TeamColor parse(final StringReader reader) throws CommandSyntaxException {
      String id = reader.readUnquotedString();
      TeamColor result = TeamColor.byName(id);
      if (result == null) {
         throw ERROR_INVALID_VALUE.createWithContext(reader, id);
      } else {
         return result;
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(TeamColor.VALUES.stream().map(TeamColor::getSerializedName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<TeamColorArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("red", "green");
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.color.invalid", value));
   }
}
