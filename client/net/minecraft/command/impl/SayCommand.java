package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class SayCommand {
   public static void func_198625_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("say").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("message", MessageArgument.func_197123_a()).executes((var0x) -> {
         ITextComponent var1 = MessageArgument.func_197124_a(var0x, "message");
         ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_148539_a(new TextComponentTranslation("chat.type.announcement", new Object[]{((CommandSource)var0x.getSource()).func_197019_b(), var1}));
         return 1;
      })));
   }
}
