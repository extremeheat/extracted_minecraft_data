package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardSlotArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("sidebar", "foo.bar");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.scoreboardDisplaySlot.invalid", new Object[]{var0});
   });

   private ScoreboardSlotArgument() {
   }

   public static ScoreboardSlotArgument displaySlot() {
      return new ScoreboardSlotArgument();
   }

   public static int getDisplaySlot(CommandContext var0, String var1) {
      return (Integer)var0.getArgument(var1, Integer.class);
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      int var3 = Scoreboard.getDisplaySlotByName(var2);
      if (var3 == -1) {
         throw ERROR_INVALID_VALUE.create(var2);
      } else {
         return var3;
      }
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(Scoreboard.getDisplaySlotNames(), var2);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
