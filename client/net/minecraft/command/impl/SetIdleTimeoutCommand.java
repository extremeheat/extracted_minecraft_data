package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class SetIdleTimeoutCommand {
   public static void func_198690_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("setidletimeout").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).then(Commands.func_197056_a("minutes", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198693_a((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "minutes"));
      })));
   }

   private static int func_198693_a(CommandSource var0, int var1) {
      var0.func_197028_i().func_143006_e(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.setidletimeout.success", new Object[]{var1}), true);
      return var1;
   }
}
