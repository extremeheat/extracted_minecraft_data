package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class MeCommand {
   public static void func_198364_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)Commands.func_197057_a("me").then(Commands.func_197056_a("action", StringArgumentType.greedyString()).executes((var0x) -> {
         ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_148539_a(new TextComponentTranslation("chat.type.emote", new Object[]{((CommandSource)var0x.getSource()).func_197019_b(), StringArgumentType.getString(var0x, "action")}));
         return 1;
      })));
   }
}
