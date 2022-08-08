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
         MessageArgument.ChatMessage var1 = MessageArgument.getChatMessage(var0x, "action");
         CommandSourceStack var2 = (CommandSourceStack)var0x.getSource();
         PlayerList var3 = var2.getServer().getPlayerList();
         var1.resolve(var2, (var2x) -> {
            var3.broadcastChatMessage(var2x, var2, ChatType.bind(ChatType.EMOTE_COMMAND, var2));
         });
         return 1;
      })));
   }
}
