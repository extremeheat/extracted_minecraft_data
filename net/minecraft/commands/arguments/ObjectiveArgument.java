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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;

public class ObjectiveArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.objective.notFound", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.objective.readonly", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType ERROR_OBJECTIVE_NAME_TOO_LONG = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.scoreboard.objectives.add.longName", new Object[]{var0});
   });

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static Objective getObjective(CommandContext var0, String var1) throws CommandSyntaxException {
      String var2 = (String)var0.getArgument(var1, String.class);
      ServerScoreboard var3 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      Objective var4 = var3.getObjective(var2);
      if (var4 == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create(var2);
      } else {
         return var4;
      }
   }

   public static Objective getWritableObjective(CommandContext var0, String var1) throws CommandSyntaxException {
      Objective var2 = getObjective(var0, var1);
      if (var2.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create(var2.getName());
      } else {
         return var2;
      }
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      if (var2.length() > 16) {
         throw ERROR_OBJECTIVE_NAME_TOO_LONG.create(16);
      } else {
         return var2;
      }
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof CommandSourceStack) {
         return SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)var1.getSource()).getServer().getScoreboard().getObjectiveNames(), var2);
      } else if (var1.getSource() instanceof SharedSuggestionProvider) {
         SharedSuggestionProvider var3 = (SharedSuggestionProvider)var1.getSource();
         return var3.customSuggestion(var1, var2);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
