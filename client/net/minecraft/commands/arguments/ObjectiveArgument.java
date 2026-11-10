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
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ObjectiveArgument implements SerializableArgument.ContextFree<String> {
   private static final ObjectiveArgument INSTANCE = new ObjectiveArgument();
   public static final SerializableArgument.Type<ObjectiveArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND;
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY;

   private ObjectiveArgument() {
      super();
   }

   public SerializableArgument.Type<ObjectiveArgument> type() {
      return TYPE;
   }

   public static ArgumentType<String> objective() {
      return INSTANCE.toBrigadier();
   }

   public static Objective getObjective(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      String id = (String)context.getArgument(name, String.class);
      Scoreboard scoreboard = ((CommandSourceStack)context.getSource()).getServer().getScoreboard();
      Objective objective = scoreboard.getObjective(id);
      if (objective == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create(id);
      } else {
         return objective;
      }
   }

   public static Objective getWritableObjective(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      Objective objective = getObjective(context, name);
      if (objective.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create(objective.getName());
      } else {
         return objective;
      }
   }

   public String parse(final StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return ((SuggestionContext)context.getSource()).suggestObjectiveNames(context, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ObjectiveArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("foo", "*", "012");
      ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((name) -> Component.translatableEscape("arguments.objective.notFound", name));
      ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((name) -> Component.translatableEscape("arguments.objective.readonly", name));
   }
}
