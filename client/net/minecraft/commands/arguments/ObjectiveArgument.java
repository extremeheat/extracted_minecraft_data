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
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("arguments.objective.notFound", var0));
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("arguments.objective.readonly", var0));

   public ObjectiveArgument() {
      super();
   }

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static Objective getObjective(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      String var2 = (String)var0.getArgument(var1, String.class);
      ServerScoreboard var3 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      Objective var4 = ((Scoreboard)var3).getObjective(var2);
      if (var4 == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create(var2);
      } else {
         return var4;
      }
   }

   public static Objective getWritableObjective(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Objective var2 = getObjective(var0, var1);
      if (var2.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create(var2.getName());
      } else {
         return var2;
      }
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      Object var3 = var1.getSource();
      if (var3 instanceof CommandSourceStack var4) {
         return SharedSuggestionProvider.suggest(var4.getServer().getScoreboard().getObjectiveNames(), var2);
      } else if (var3 instanceof SharedSuggestionProvider var5) {
         return var5.customSuggestion(var1);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
