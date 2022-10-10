package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class ReloadCommand {
   public static void func_198597_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("reload").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).executes((var0x) -> {
         ((CommandSource)var0x.getSource()).func_197030_a(new TextComponentTranslation("commands.reload.success", new Object[0]), true);
         ((CommandSource)var0x.getSource()).func_197028_i().func_193031_aM();
         return 0;
      }));
   }
}
