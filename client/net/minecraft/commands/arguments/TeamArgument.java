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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamArgument implements SerializableArgument.ContextFree<String> {
   private static final TeamArgument INSTANCE = new TeamArgument();
   public static final SerializableArgument.Type<TeamArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND;

   private TeamArgument() {
      super();
   }

   public SerializableArgument.Type<TeamArgument> type() {
      return TYPE;
   }

   public static ArgumentType<String> team() {
      return INSTANCE.toBrigadier();
   }

   public static PlayerTeam getTeam(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      String id = (String)context.getArgument(name, String.class);
      Scoreboard scoreboard = ((CommandSourceStack)context.getSource()).getServer().getScoreboard();
      PlayerTeam team = scoreboard.getPlayerTeam(id);
      if (team == null) {
         throw ERROR_TEAM_NOT_FOUND.create(id);
      } else {
         return team;
      }
   }

   public String parse(final StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(((SuggestionContext)context.getSource()).allTeams(), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<TeamArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("foo", "123");
      ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((name) -> Component.translatableEscape("team.notFound", name));
   }
}
