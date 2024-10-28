package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.players.PlayerList;

public class SayCommand {
   public SayCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         MessageArgument.resolveChatMessage(var0x, "message", (var1) -> {
            CommandSourceStack var2 = (CommandSourceStack)var0x.getSource();
            PlayerList var3 = var2.getServer().getPlayerList();
            var3.broadcastChatMessage(var1, var2, ChatType.bind(ChatType.SAY_COMMAND, var2));
         });
         return 1;
      })));
   }
}
