package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;

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

      for(CommandFunction var4 : var1) {
         var2 += var0.getServer().getFunctions().execute(var4, var0.withSuppressedOutput().withMaximumPermission(2));
      }

      if (var1.size() == 1) {
         var0.sendSuccess(Component.translatable("commands.function.success.single", var2, ((CommandFunction)var1.iterator().next()).getId()), true);
      } else {
         var0.sendSuccess(Component.translatable("commands.function.success.multiple", var2, var1.size()), true);
      }

      return var2;
   }
}
