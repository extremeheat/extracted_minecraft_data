package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class KillCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).executes((var0x) -> {
         return kill((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"));
      })));
   }

   private static int kill(CommandSourceStack var0, Collection<? extends Entity> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var3.kill();
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.kill.success.single", new Object[]{((Entity)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.kill.success.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }
}
