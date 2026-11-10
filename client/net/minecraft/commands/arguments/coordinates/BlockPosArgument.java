package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class BlockPosArgument implements SerializableArgument.ContextFree<Coordinates> {
   public static final BlockPosArgument INSTANCE = new BlockPosArgument();
   public static final SerializableArgument.Type<BlockPosArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_LOADED;
   public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD;
   public static final SimpleCommandExceptionType ERROR_OUT_OF_BOUNDS;

   private BlockPosArgument() {
      super();
   }

   public SerializableArgument.Type<BlockPosArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Coordinates> blockPos() {
      return INSTANCE.toBrigadier();
   }

   public static BlockPos getLoadedBlockPos(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      ServerLevel level = ((CommandSourceStack)context.getSource()).getLevel();
      return getLoadedBlockPos(context, level, name);
   }

   public static BlockPos getLoadedBlockPos(final CommandContext<CommandSourceStack> context, final ServerLevel level, final String name) throws CommandSyntaxException {
      BlockPos pos = getBlockPos(context, name);
      if (!level.hasChunkAt(pos)) {
         throw ERROR_NOT_LOADED.create();
      } else if (!level.isInWorldBounds(pos)) {
         throw ERROR_OUT_OF_WORLD.create();
      } else {
         return pos;
      }
   }

   public static BlockPos getBlockPos(final CommandContext<CommandSourceStack> context, final String name) {
      return ((Coordinates)context.getArgument(name, Coordinates.class)).getBlockPos((CommandSourceStack)context.getSource());
   }

   public static BlockPos getSpawnablePos(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      BlockPos pos = getBlockPos(context, name);
      if (!Level.isInSpawnableBounds(pos)) {
         throw ERROR_OUT_OF_BOUNDS.create();
      } else {
         return pos;
      }
   }

   public Coordinates parse(final StringReader reader) throws CommandSyntaxException {
      return (Coordinates)(reader.canRead() && reader.peek() == '^' ? LocalCoordinates.parse(reader) : WorldCoordinates.parseInt(reader));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      String remainder = builder.getRemaining();
      Collection<SuggestionContext.TextCoordinates> suggestedCoordinates;
      if (!remainder.isEmpty() && remainder.charAt(0) == '^') {
         suggestedCoordinates = Collections.singleton(SuggestionContext.TextCoordinates.DEFAULT_LOCAL);
      } else {
         suggestedCoordinates = ((SuggestionContext)context.getSource()).relevantCoordinates();
      }

      return SuggestionContext.suggestCoordinates(remainder, suggestedCoordinates, builder, Commands.createValidator(this::parse));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<BlockPosArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
      ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
      ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofworld"));
      ERROR_OUT_OF_BOUNDS = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofbounds"));
   }
}
