package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.OptionalInt;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (var0, var1) -> {
      ServerFunctionManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(var2.getTagNames(), var1, "#");
      return SharedSuggestionProvider.suggestResource(var2.getFunctionNames(), var1);
   };

   public FunctionCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("name", FunctionArgument.functions())
                  .suggests(SUGGEST_FUNCTION)
                  .executes(var0x -> runFunction((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctions(var0x, "name")))
            )
      );
   }

   private static int runFunction(CommandSourceStack var0, Collection<CommandFunction> var1) {
      int var2 = 0;
      boolean var3 = false;

      for(CommandFunction var5 : var1) {
         MutableObject var6 = new MutableObject(OptionalInt.empty());
         int var7 = var0.getServer()
            .getFunctions()
            .execute(var5, var0.withSuppressedOutput().withMaximumPermission(2).withReturnValueConsumer(var1x -> var6.setValue(OptionalInt.of(var1x))));
         OptionalInt var8 = (OptionalInt)var6.getValue();
         var2 += var8.orElse(var7);
         var3 |= var8.isPresent();
      }

      int var9 = var2;
      if (var1.size() == 1) {
         if (var3) {
            var0.sendSuccess(
               () -> Component.translatable("commands.function.success.single.result", var9, ((CommandFunction)var1.iterator().next()).getId()), true
            );
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.function.success.single", var9, ((CommandFunction)var1.iterator().next()).getId()), true);
         }
      } else if (var3) {
         var0.sendSuccess(() -> Component.translatable("commands.function.success.multiple.result", var1.size()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.function.success.multiple", var9, var1.size()), true);
      }

      return var2;
   }
}
