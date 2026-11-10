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
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

public class GameModeArgument implements SerializableArgument.ContextFree<GameType> {
   private static final GameModeArgument INSTANCE = new GameModeArgument();
   public static final SerializableArgument.Type<GameModeArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final GameType[] VALUES;
   private static final DynamicCommandExceptionType ERROR_INVALID;

   private GameModeArgument() {
      super();
   }

   public SerializableArgument.Type<GameModeArgument> type() {
      return TYPE;
   }

   public GameType parse(final StringReader reader) throws CommandSyntaxException {
      String gameTypeString = reader.readUnquotedString();
      GameType gameType = GameType.byName(gameTypeString, (GameType)null);
      if (gameType == null) {
         throw ERROR_INVALID.createWithContext(reader, gameTypeString);
      } else {
         return gameType;
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(Arrays.stream(VALUES).map(GameType::getName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ArgumentType<GameType> gameMode() {
      return INSTANCE.toBrigadier();
   }

   public static GameType getGameMode(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return (GameType)context.getArgument(name, GameType.class);
   }

   static {
      TYPE = SerializableArgument.<GameModeArgument>singletonType(INSTANCE);
      EXAMPLES = (Collection)Stream.of(GameType.SURVIVAL, GameType.CREATIVE).map(GameType::getName).collect(Collectors.toList());
      VALUES = GameType.values();
      ERROR_INVALID = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.gamemode.invalid", value));
   }
}
