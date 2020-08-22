package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class EmoteCommands {
   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((var0x) -> {
         ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.emote", new Object[]{((CommandSourceStack)var0x.getSource()).getDisplayName(), StringArgumentType.getString(var0x, "action")}));
         return 1;
      })));
   }
}
