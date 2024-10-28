package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SetPlayerIdleTimeoutCommand {
   public SetPlayerIdleTimeoutCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setidletimeout").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.argument("minutes", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setIdleTimeout((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "minutes"));
      })));
   }

   private static int setIdleTimeout(CommandSourceStack var0, int var1) {
      var0.getServer().setPlayerIdleTimeout(var1);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.setidletimeout.success", var1);
      }, true);
      return var1;
   }
}
