package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class ReloadCommand {
   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         ((CommandSourceStack)var0x.getSource()).sendSuccess(new TranslatableComponent("commands.reload.success", new Object[0]), true);
         ((CommandSourceStack)var0x.getSource()).getServer().reloadResources();
         return 0;
      }));
   }
}
