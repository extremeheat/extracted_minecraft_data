package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class StopCommand {
   public StopCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stop").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).executes((var0x) -> {
         ((CommandSourceStack)var0x.getSource()).sendSuccess(() -> {
            return Component.translatable("commands.stop.stopping");
         }, true);
         ((CommandSourceStack)var0x.getSource()).getServer().halt(false);
         return 1;
      }));
   }
}
