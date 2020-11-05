package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (var0, var1) -> {
      ServerFunctionManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(var2.getTagNames(), var1, "#");
      return SharedSuggestionProvider.suggestResource(var2.getFunctionNames(), var1);
   };

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes((var0x) -> {
         return runFunction((CommandSourceStack)var0x.getSource(), FunctionArgument.getFunctions(var0x, "name"));
      })));
   }

   private static int runFunction(CommandSourceStack var0, Collection<CommandFunction> var1) {
      int var2 = 0;

      CommandFunction var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var0.getServer().getFunctions().execute(var4, var0.withSuppressedOutput().withMaximumPermission(2))) {
         var4 = (CommandFunction)var3.next();
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.function.success.single", new Object[]{var2, ((CommandFunction)var1.iterator().next()).getId()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.function.success.multiple", new Object[]{var2, var1.size()}), true);
      }

      return var2;
   }
}
