package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument implements SerializableArgument<EntitySelector> {
   public static final MapCodec<EntityArgument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("single_result").forGetter((o) -> o.singleResult), Codec.BOOL.fieldOf("players_only").forGetter((o) -> o.playersOnly)).apply(i, EntityArgument::new));
   public static final StreamCodec<ByteBuf, EntityArgument> STREAM_CODEC = new StreamCodec<ByteBuf, EntityArgument>() {
      private static final byte FLAG_SINGLE_RESULT = 1;
      private static final byte FLAG_PLAYERS_ONLY = 2;

      public EntityArgument decode(final ByteBuf input) {
         byte flags = input.readByte();
         return new EntityArgument((flags & 1) != 0, (flags & 2) != 0);
      }

      public void encode(final ByteBuf output, final EntityArgument value) {
         int flags = 0;
         if (value.singleResult) {
            flags |= 1;
         }

         if (value.playersOnly) {
            flags |= 2;
         }

         output.writeByte(flags);
      }
   };
   public static final SerializableArgument.Type<EntityArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY;
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER;
   public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED;
   public static final SimpleCommandExceptionType NO_ENTITIES_FOUND;
   public static final SimpleCommandExceptionType NO_PLAYERS_FOUND;
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED;
   private final boolean singleResult;
   private final boolean playersOnly;

   protected EntityArgument(final boolean singleResult, final boolean playersOnly) {
      super();
      this.singleResult = singleResult;
      this.playersOnly = playersOnly;
   }

   public SerializableArgument.Type<EntityArgument> type() {
      return TYPE;
   }

   public static ArgumentType<EntitySelector> entity() {
      return (new EntityArgument(true, false)).toBrigadier();
   }

   public static Entity getEntity(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).findSingleEntity((CommandSourceStack)context.getSource());
   }

   public static ArgumentType<EntitySelector> entities() {
      return (new EntityArgument(false, false)).toBrigadier();
   }

   public static Collection<? extends Entity> getEntities(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      Collection<? extends Entity> result = getOptionalEntities(context, name);
      if (result.isEmpty()) {
         throw NO_ENTITIES_FOUND.create();
      } else {
         return result;
      }
   }

   public static Collection<? extends Entity> getOptionalEntities(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).findEntities((CommandSourceStack)context.getSource());
   }

   public static Collection<ServerPlayer> getOptionalPlayers(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).findPlayers((CommandSourceStack)context.getSource());
   }

   public static ArgumentType<EntitySelector> player() {
      return (new EntityArgument(true, true)).toBrigadier();
   }

   public static ServerPlayer getPlayer(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).findSinglePlayer((CommandSourceStack)context.getSource());
   }

   public static ArgumentType<EntitySelector> players() {
      return (new EntityArgument(false, true)).toBrigadier();
   }

   public static Collection<ServerPlayer> getPlayers(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      List<ServerPlayer> players = ((EntitySelector)context.getArgument(name, EntitySelector.class)).findPlayers((CommandSourceStack)context.getSource());
      if (players.isEmpty()) {
         throw NO_PLAYERS_FOUND.create();
      } else {
         return players;
      }
   }

   public static Stream<? extends Entity> getSuggestedEntities(final MinecraftServer server, final CommandContext<? extends SuggestionContext> context, final String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).findSuggestions(server, (SuggestionContext)context.getSource());
   }

   public EntitySelector parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      return this.parse(reader, context.allowsSelectors());
   }

   private EntitySelector parse(final StringReader reader, final boolean allowSelectors) throws CommandSyntaxException {
      int start = 0;
      EntitySelectorParser parser = new EntitySelectorParser(reader, allowSelectors);
      EntitySelector selector = parser.parse();
      if (selector.getMaxResults() > 1 && this.singleResult) {
         if (this.playersOnly) {
            reader.setCursor(0);
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(reader);
         } else {
            reader.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext(reader);
         }
      } else if (selector.includesEntities() && this.playersOnly && !selector.isSelfSelector()) {
         reader.setCursor(0);
         throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(reader);
      } else {
         return selector;
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      SuggestionContext source = (SuggestionContext)context.getSource();
      StringReader reader = new StringReader(builder.getInput());
      reader.setCursor(builder.getStart());
      EntitySelectorParser parser = new EntitySelectorParser(reader, source.allowsSelectors());

      try {
         parser.parse();
      } catch (CommandSyntaxException var7) {
      }

      return parser.fillSuggestions(builder, (suggestions) -> {
         Collection<String> onlinePlayerNames = source.onlinePlayerNames();
         Iterable<String> suggestedNames = (Iterable<String>)(this.playersOnly ? onlinePlayerNames : Iterables.concat(onlinePlayerNames, source.selectedEntities()));
         SuggestionContext.suggest(suggestedNames, suggestions);
      });
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = new SerializableArgument.Type<EntityArgument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
      ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(Component.translatable("argument.entity.toomany"));
      ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.toomany"));
      ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.player.entities"));
      NO_ENTITIES_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.entity"));
      NO_PLAYERS_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.player"));
      ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.entity.selector.not_allowed"));
   }
}
