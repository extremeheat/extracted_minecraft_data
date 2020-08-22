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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class BlockPosArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.unloaded", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.outofworld", new Object[0]));

   public static BlockPosArgument blockPos() {
      return new BlockPosArgument();
   }

   public static BlockPos getLoadedBlockPos(CommandContext var0, String var1) throws CommandSyntaxException {
      BlockPos var2 = ((Coordinates)var0.getArgument(var1, Coordinates.class)).getBlockPos((CommandSourceStack)var0.getSource());
      if (!((CommandSourceStack)var0.getSource()).getLevel().hasChunkAt(var2)) {
         throw ERROR_NOT_LOADED.create();
      } else {
         ((CommandSourceStack)var0.getSource()).getLevel();
         if (!ServerLevel.isInWorldBounds(var2)) {
            throw ERROR_OUT_OF_WORLD.create();
         } else {
            return var2;
         }
      }
   }

   public static BlockPos getOrLoadBlockPos(CommandContext var0, String var1) throws CommandSyntaxException {
      return ((Coordinates)var0.getArgument(var1, Coordinates.class)).getBlockPos((CommandSourceStack)var0.getSource());
   }

   public Coordinates parse(StringReader var1) throws CommandSyntaxException {
      return (Coordinates)(var1.canRead() && var1.peek() == '^' ? LocalCoordinates.parse(var1) : WorldCoordinates.parseInt(var1));
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
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

         return SharedSuggestionProvider.suggestCoordinates(var3, (Collection)var4, var2, Commands.createValidator(this::parse));
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
