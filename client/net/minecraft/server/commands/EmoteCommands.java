package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.players.PlayerList;

public class EmoteCommands {
   public EmoteCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)Commands.literal("me").then(Commands.argument("action", MessageArgument.message()).executes((var0x) -> {
         MessageArgument.resolveChatMessage(var0x, "action", (var1) -> {
            CommandSourceStack var2 = (CommandSourceStack)var0x.getSource();
            PlayerList var3 = var2.getServer().getPlayerList();
            var3.broadcastChatMessage(var1, var2, ChatType.bind(ChatType.EMOTE_COMMAND, var2));
         });
         return 1;
      })));
   }
}
