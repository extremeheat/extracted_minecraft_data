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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

public class GameModeArgument implements ArgumentType<GameType> {
   private static final Collection<String> EXAMPLES = Stream.of(GameType.SURVIVAL, GameType.CREATIVE).map(GameType::getName).collect(Collectors.toList());
   private static final GameType[] VALUES = GameType.values();
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
      var0 -> Component.translatable("argument.gamemode.invalid", var0)
   );

   public GameModeArgument() {
      super();
   }

   public GameType parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      GameType var3 = GameType.byName(var2, null);
      if (var3 == null) {
         throw ERROR_INVALID.createWithContext(var1, var2);
      } else {
         return var3;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return var1.getSource() instanceof SharedSuggestionProvider
         ? SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(GameType::getName), var2)
         : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static GameModeArgument gameMode() {
      return new GameModeArgument();
   }

   public static GameType getGameMode(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return (GameType)var0.getArgument(var1, GameType.class);
   }
}
