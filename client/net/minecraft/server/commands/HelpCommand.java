package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HelpCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.help.failed"));

   public HelpCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((var1) -> {
         Map var2 = var0.getSmartUsage(var0.getRoot(), (CommandSourceStack)var1.getSource());
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ((CommandSourceStack)var1.getSource()).sendSuccess(() -> {
               return Component.literal("/" + var4);
            }, false);
         }

         return var2.size();
      })).then(Commands.argument("command", StringArgumentType.greedyString()).executes((var1) -> {
         ParseResults var2 = var0.parse(StringArgumentType.getString(var1, "command"), (CommandSourceStack)var1.getSource());
         if (var2.getContext().getNodes().isEmpty()) {
            throw ERROR_FAILED.create();
         } else {
            Map var3 = var0.getSmartUsage(((ParsedCommandNode)Iterables.getLast(var2.getContext().getNodes())).getNode(), (CommandSourceStack)var1.getSource());
            Iterator var4 = var3.values().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               ((CommandSourceStack)var1.getSource()).sendSuccess(() -> {
                  String var10000 = var2.getReader().getString();
                  return Component.literal("/" + var10000 + " " + var5);
               }, false);
            }

            return var3.size();
         }
      })));
   }
}
