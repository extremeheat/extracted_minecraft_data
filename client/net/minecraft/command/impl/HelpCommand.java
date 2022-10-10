package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class HelpCommand {
   private static final SimpleCommandExceptionType field_206930_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.help.failed", new Object[0]));

   public static void func_198510_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("help").executes((var1) -> {
         Map var2 = var0.getSmartUsage(var0.getRoot(), var1.getSource());
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ((CommandSource)var1.getSource()).func_197030_a(new TextComponentString("/" + var4), false);
         }

         return var2.size();
      })).then(Commands.func_197056_a("command", StringArgumentType.greedyString()).executes((var1) -> {
         ParseResults var2 = var0.parse(StringArgumentType.getString(var1, "command"), var1.getSource());
         if (var2.getContext().getNodes().isEmpty()) {
            throw field_206930_a.create();
         } else {
            Map var3 = var0.getSmartUsage((CommandNode)Iterables.getLast(var2.getContext().getNodes().keySet()), var1.getSource());
            Iterator var4 = var3.values().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               ((CommandSource)var1.getSource()).func_197030_a(new TextComponentString("/" + var2.getReader().getString() + " " + var5), false);
            }

            return var3.size();
         }
      })));
   }
}
