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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class HelpCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.help.failed", new Object[0]));

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((var1) -> {
         Map var2 = var0.getSmartUsage(var0.getRoot(), var1.getSource());
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ((CommandSourceStack)var1.getSource()).sendSuccess(new TextComponent("/" + var4), false);
         }

         return var2.size();
      })).then(Commands.argument("command", StringArgumentType.greedyString()).executes((var1) -> {
         ParseResults var2 = var0.parse(StringArgumentType.getString(var1, "command"), var1.getSource());
         if (var2.getContext().getNodes().isEmpty()) {
            throw ERROR_FAILED.create();
         } else {
            Map var3 = var0.getSmartUsage(((ParsedCommandNode)Iterables.getLast(var2.getContext().getNodes())).getNode(), var1.getSource());
            Iterator var4 = var3.values().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               ((CommandSourceStack)var1.getSource()).sendSuccess(new TextComponent("/" + var2.getReader().getString() + " " + var5), false);
            }

            return var3.size();
         }
      })));
   }
}
