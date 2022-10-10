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
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> field_201317_b = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType field_197159_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.objective.notFound", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_197160_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.objective.readonly", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_200379_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.scoreboard.objectives.add.longName", new Object[]{var0});
   });

   public ObjectiveArgument() {
      super();
   }

   public static ObjectiveArgument func_197157_a() {
      return new ObjectiveArgument();
   }

   public static ScoreObjective func_197158_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      String var2 = (String)var0.getArgument(var1, String.class);
      ServerScoreboard var3 = ((CommandSource)var0.getSource()).func_197028_i().func_200251_aP();
      ScoreObjective var4 = var3.func_96518_b(var2);
      if (var4 == null) {
         throw field_197159_a.create(var2);
      } else {
         return var4;
      }
   }

   public static ScoreObjective func_197156_b(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      ScoreObjective var2 = func_197158_a(var0, var1);
      if (var2.func_96680_c().func_96637_b()) {
         throw field_197160_b.create(var2.func_96679_b());
      } else {
         return var2;
      }
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      if (var2.length() > 16) {
         throw field_200379_a.create(16);
      } else {
         return var2;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof CommandSource) {
         return ISuggestionProvider.func_197005_b(((CommandSource)var1.getSource()).func_197028_i().func_200251_aP().func_197897_d(), var2);
      } else if (var1.getSource() instanceof ISuggestionProvider) {
         ISuggestionProvider var3 = (ISuggestionProvider)var1.getSource();
         return var3.func_197009_a(var1, var2);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return field_201317_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
