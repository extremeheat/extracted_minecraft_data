package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class SeedCommand {
   public static void func_198671_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("seed").requires((var0x) -> {
         return var0x.func_197028_i().func_71264_H() || var0x.func_197034_c(2);
      })).executes((var0x) -> {
         long var1 = ((CommandSource)var0x.getSource()).func_197023_e().func_72905_C();
         ITextComponent var3 = TextComponentUtils.func_197676_a((new TextComponentString(String.valueOf(var1))).func_211710_a((var2) -> {
            var2.func_150238_a(TextFormatting.GREEN).func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(var1))).func_179989_a(String.valueOf(var1));
         }));
         ((CommandSource)var0x.getSource()).func_197030_a(new TextComponentTranslation("commands.seed.success", new Object[]{var3}), false);
         return (int)var1;
      }));
   }
}
