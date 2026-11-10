package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;

public class GameProfileArgument implements SerializableArgument<Result> {
   private static final GameProfileArgument INSTANCE = new GameProfileArgument();
   public static final SerializableArgument.Type<GameProfileArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER;

   private GameProfileArgument() {
      super();
   }

   public SerializableArgument.Type<GameProfileArgument> type() {
      return TYPE;
   }

   public static Collection<NameAndId> getGameProfiles(final CommandContext<CommandSourceStack> source, final String name) throws CommandSyntaxException {
      return ((Result)source.getArgument(name, Result.class)).getNames((CommandSourceStack)source.getSource());
   }

   public static ArgumentType<Result> gameProfile() {
      return INSTANCE.toBrigadier();
   }

   public Result parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      return parse(reader, context.allowsSelectors());
   }

   private static Result parse(final StringReader reader, final boolean allowSelectors) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '@') {
         EntitySelectorParser parser = new EntitySelectorParser(reader, allowSelectors);
         EntitySelector parse = parser.parse();
         if (parse.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(reader);
         } else {
            return new SelectorResult(parse);
         }
      } else {
         int start = reader.getCursor();

         while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
         }

         String name = reader.getString().substring(start, reader.getCursor());
         return (c) -> {
            Optional<NameAndId> result = c.getServer().services().nameToIdCache().get(name);
            SimpleCommandExceptionType var10001 = ERROR_UNKNOWN_PLAYER;
            Objects.requireNonNull(var10001);
            return Collections.singleton((NameAndId)result.orElseThrow(var10001::create));
         };
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      SuggestionContext source = (SuggestionContext)context.getSource();
      StringReader reader = new StringReader(builder.getInput());
      reader.setCursor(builder.getStart());
      EntitySelectorParser parser = new EntitySelectorParser(reader, source.permissions().hasPermission(Permissions.COMMANDS_ENTITY_SELECTORS));

      try {
         parser.parse();
      } catch (CommandSyntaxException var7) {
      }

      return parser.fillSuggestions(builder, (suggestions) -> SuggestionContext.suggest(source.onlinePlayerNames(), suggestions));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<GameProfileArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
      ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.unknown"));
   }

   public static class SelectorResult implements Result {
      private final EntitySelector selector;

      public SelectorResult(final EntitySelector selector) {
         super();
         this.selector = selector;
      }

      public Collection<NameAndId> getNames(final CommandSourceStack sender) throws CommandSyntaxException {
         List<ServerPlayer> players = this.selector.findPlayers(sender);
         if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            List<NameAndId> result = new ArrayList();

            for(ServerPlayer entity : players) {
               result.add(entity.nameAndId());
            }

            return result;
         }
      }
   }

   @FunctionalInterface
   public interface Result {
      Collection<NameAndId> getNames(final CommandSourceStack sender) throws CommandSyntaxException;
   }
}
