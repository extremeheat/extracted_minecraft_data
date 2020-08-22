package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SayCommand {
   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         Component var1 = MessageArgument.getMessage(var0x, "message");
         ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.announcement", new Object[]{((CommandSourceStack)var0x.getSource()).getDisplayName(), var1}));
         return 1;
      })));
   }
}
