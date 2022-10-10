package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class MessageCommand {
   public static void func_198537_a(CommandDispatcher<CommandSource> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.func_197057_a("msg").then(Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197056_a("message", MessageArgument.func_197123_a()).executes((var0x) -> {
         return func_198538_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), MessageArgument.func_197124_a(var0x, "message"));
      }))));
      var0.register((LiteralArgumentBuilder)Commands.func_197057_a("tell").redirect(var1));
      var0.register((LiteralArgumentBuilder)Commands.func_197057_a("w").redirect(var1));
   }

   private static int func_198538_a(CommandSource var0, Collection<EntityPlayerMP> var1, ITextComponent var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         var4.func_145747_a((new TextComponentTranslation("commands.message.display.incoming", new Object[]{var0.func_197019_b(), var2.func_212638_h()})).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
         var0.func_197030_a((new TextComponentTranslation("commands.message.display.outgoing", new Object[]{var4.func_145748_c_(), var2.func_212638_h()})).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
      }

      return var1.size();
   }
}
