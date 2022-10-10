package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class TeamArgument implements ArgumentType<String> {
   private static final Collection<String> field_201330_a = Arrays.asList("foo", "123");
   private static final DynamicCommandExceptionType field_197229_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("team.notFound", new Object[]{var0});
   });

   public TeamArgument() {
      super();
   }

   public static TeamArgument func_197227_a() {
      return new TeamArgument();
   }

   public static ScorePlayerTeam func_197228_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      String var2 = (String)var0.getArgument(var1, String.class);
      ServerScoreboard var3 = ((CommandSource)var0.getSource()).func_197028_i().func_200251_aP();
      ScorePlayerTeam var4 = var3.func_96508_e(var2);
      if (var4 == null) {
         throw field_197229_a.create(var2);
      } else {
         return var4;
      }
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return var1.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.func_197005_b(((ISuggestionProvider)var1.getSource()).func_197012_k(), var2) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return field_201330_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
