package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class BlockPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_201333_c = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType field_197278_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.unloaded", new Object[0]));
   public static final SimpleCommandExceptionType field_197279_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.outofworld", new Object[0]));

   public BlockPosArgument() {
      super();
   }

   public static BlockPosArgument func_197276_a() {
      return new BlockPosArgument();
   }

   public static BlockPos func_197273_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      BlockPos var2 = ((ILocationArgument)var0.getArgument(var1, ILocationArgument.class)).func_197280_c((CommandSource)var0.getSource());
      if (!((CommandSource)var0.getSource()).func_197023_e().func_175667_e(var2)) {
         throw field_197278_b.create();
      } else {
         ((CommandSource)var0.getSource()).func_197023_e();
         if (!WorldServer.func_175701_a(var2)) {
            throw field_197279_c.create();
         } else {
            return var2;
         }
      }
   }

   public static BlockPos func_197274_b(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((ILocationArgument)var0.getArgument(var1, ILocationArgument.class)).func_197280_c((CommandSource)var0.getSource());
   }

   public ILocationArgument parse(StringReader var1) throws CommandSyntaxException {
      return (ILocationArgument)(var1.canRead() && var1.peek() == '^' ? LocalLocationArgument.func_200142_a(var1) : LocationInput.func_200148_a(var1));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (!(var1.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String var3 = var2.getRemaining();
         Object var4;
         if (!var3.isEmpty() && var3.charAt(0) == '^') {
            var4 = Collections.singleton(ISuggestionProvider.Coordinates.field_209004_a);
         } else {
            var4 = ((ISuggestionProvider)var1.getSource()).func_199613_a(false);
         }

         return ISuggestionProvider.func_209000_a(var3, (Collection)var4, var2, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return field_201333_c;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
