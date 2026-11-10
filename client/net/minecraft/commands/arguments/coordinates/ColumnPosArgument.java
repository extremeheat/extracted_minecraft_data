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
import net.minecraft.server.level.ColumnPos;

public class ColumnPosArgument implements SerializableArgument.ContextFree<Coordinates> {
   private static final ColumnPosArgument INSTANCE = new ColumnPosArgument();
   public static final SerializableArgument.Type<ColumnPosArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE;

   private ColumnPosArgument() {
      super();
   }

   public SerializableArgument.Type<ColumnPosArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Coordinates> columnPos() {
      return INSTANCE.toBrigadier();
   }

   public static ColumnPos getColumnPos(final CommandContext<CommandSourceStack> context, final String name) {
      BlockPos pos = ((Coordinates)context.getArgument(name, Coordinates.class)).getBlockPos((CommandSourceStack)context.getSource());
      return new ColumnPos(pos.getX(), pos.getZ());
   }

   public Coordinates parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      if (!reader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(reader);
      } else {
         WorldCoordinate x = WorldCoordinate.parseInt(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate z = WorldCoordinate.parseInt(reader);
            return new WorldCoordinates(x, new WorldCoordinate(true, 0.0), z);
         } else {
            reader.setCursor(start);
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
         }
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      String remainder = builder.getRemaining();
      Collection<SuggestionContext.TextCoordinates> suggestedCoordinates;
      if (!remainder.isEmpty() && remainder.charAt(0) == '^') {
         suggestedCoordinates = Collections.singleton(SuggestionContext.TextCoordinates.DEFAULT_LOCAL);
      } else {
         suggestedCoordinates = ((SuggestionContext)context.getSource()).relevantCoordinates();
      }

      return SuggestionContext.suggest2DCoordinates(remainder, suggestedCoordinates, builder, Commands.createValidator(this::parse));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ColumnPosArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
      ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos2d.incomplete"));
   }
}
