package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class KickCommand {
   public static void func_198514_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("kick").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var0x) -> {
         return func_198515_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), new TextComponentTranslation("multiplayer.disconnect.kicked", new Object[0]));
      })).then(Commands.func_197056_a("reason", MessageArgument.func_197123_a()).executes((var0x) -> {
         return func_198515_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), MessageArgument.func_197124_a(var0x, "reason"));
      }))));
   }

   private static int func_198515_a(CommandSource var0, Collection<EntityPlayerMP> var1, ITextComponent var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         var4.field_71135_a.func_194028_b(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.kick.success", new Object[]{var4.func_145748_c_(), var2}), true);
      }

      return var1.size();
   }
}
