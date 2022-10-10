package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void func_198487_a(CommandDispatcher<CommandSource> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.func_197057_a("gamerule").requires((var0x) -> {
         return var0x.func_197034_c(2);
      });
      Iterator var2 = GameRules.func_196231_c().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.then(((LiteralArgumentBuilder)Commands.func_197057_a((String)var3.getKey()).executes((var1x) -> {
            return func_198492_a((CommandSource)var1x.getSource(), (String)var3.getKey());
         })).then(((GameRules.ValueDefinition)var3.getValue()).func_199594_b().func_199809_a("value").executes((var1x) -> {
            return func_198488_a((CommandSource)var1x.getSource(), (String)var3.getKey(), var1x);
         })));
      }

      var0.register(var1);
   }

   private static int func_198488_a(CommandSource var0, String var1, CommandContext<CommandSource> var2) {
      GameRules.Value var3 = var0.func_197028_i().func_200252_aR().func_196230_f(var1);
      var3.func_180254_e().func_196222_a(var2, "value", var3);
      var0.func_197030_a(new TextComponentTranslation("commands.gamerule.set", new Object[]{var1, var3.func_82756_a()}), true);
      return var3.func_180255_c();
   }

   private static int func_198492_a(CommandSource var0, String var1) {
      GameRules.Value var2 = var0.func_197028_i().func_200252_aR().func_196230_f(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.gamerule.query", new Object[]{var1, var2.func_82756_a()}), false);
      return var2.func_180255_c();
   }
}
