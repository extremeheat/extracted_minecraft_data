package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ColumnPos;

public class ColumnPosArgument implements SerializableArgument.ContextFree<Coordinates> {
   public static final ColumnPosArgument INSTANCE = new ColumnPosArgument();
   public static final SerializableArgument.Type<ColumnPosArgument> TYPE;
   private static final Collection<String> EXAMPLES;

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
      return Coordinates.parseXZ(reader, WorldCoordinate.INT_PARSER);
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      String remainder = builder.getRemaining();
      return SuggestionContext.suggestXZCoordinates(remainder, Coordinates.suggestRelevantCoordinates(context, remainder), builder, Commands.createValidator(this::parse));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ColumnPosArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   }
}
