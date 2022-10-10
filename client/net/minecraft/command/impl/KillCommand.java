package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;

public class KillCommand {
   public static void func_198518_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("kill").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).executes((var0x) -> {
         return func_198519_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"));
      })));
   }

   private static int func_198519_a(CommandSource var0, Collection<? extends Entity> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var3.func_174812_G();
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.kill.success.single", new Object[]{((Entity)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.kill.success.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }
}
