package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;

public class ColumnPosArgument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos2d.incomplete"));

   public ColumnPosArgument() {
      super();
   }

   public static ColumnPosArgument columnPos() {
      return new ColumnPosArgument();
   }

   public static ColumnPos getColumnPos(CommandContext<CommandSourceStack> var0, String var1) {
      BlockPos var2 = ((Coordinates)var0.getArgument(var1, Coordinates.class)).getBlockPos((CommandSourceStack)var0.getSource());
      return new ColumnPos(var2.getX(), var2.getZ());
   }

   public Coordinates parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      if (!var1.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(var1);
      } else {
         WorldCoordinate var3 = WorldCoordinate.parseInt(var1);
         if (var1.canRead() && var1.peek() == ' ') {
            var1.skip();
            WorldCoordinate var4 = WorldCoordinate.parseInt(var1);
            return new WorldCoordinates(var3, new WorldCoordinate(true, 0.0), var4);
         } else {
            var1.setCursor(var2);
            throw ERROR_NOT_COMPLETE.createWithContext(var1);
         }
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (!(var1.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String var3 = var2.getRemaining();
         Object var4;
         if (!var3.isEmpty() && var3.charAt(0) == '^') {
            var4 = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            var4 = ((SharedSuggestionProvider)var1.getSource()).getRelevantCoordinates();
         }

         return SharedSuggestionProvider.suggest2DCoordinates(var3, (Collection)var4, var2, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
