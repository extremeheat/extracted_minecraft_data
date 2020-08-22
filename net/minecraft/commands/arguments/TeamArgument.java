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
import net.minecraft.world.scores.PlayerTeam;

public class TeamArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "123");
   private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("team.notFound", new Object[]{var0});
   });

   public static TeamArgument team() {
      return new TeamArgument();
   }

   public static PlayerTeam getTeam(CommandContext var0, String var1) throws CommandSyntaxException {
      String var2 = (String)var0.getArgument(var1, String.class);
      ServerScoreboard var3 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      PlayerTeam var4 = var3.getPlayerTeam(var2);
      if (var4 == null) {
         throw ERROR_TEAM_NOT_FOUND.create(var2);
      } else {
         return var4;
      }
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readUnquotedString();
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      return var1.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest((Iterable)((SharedSuggestionProvider)var1.getSource()).getAllTeams(), var2) : Suggestions.empty();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
