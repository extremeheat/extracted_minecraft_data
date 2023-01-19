package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class MsgCommand {
   public MsgCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register(
         (LiteralArgumentBuilder)Commands.literal("msg")
            .then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes(var0x -> {
               Collection var1x = EntityArgument.getPlayers(var0x, "targets");
               if (!var1x.isEmpty()) {
                  MessageArgument.resolveChatMessage(var0x, "message", var2 -> sendMessage((CommandSourceStack)var0x.getSource(), var1x, var2));
               }
      
               return var1x.size();
            })))
      );
      var0.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(var1));
      var0.register((LiteralArgumentBuilder)Commands.literal("w").redirect(var1));
   }

   private static void sendMessage(CommandSourceStack var0, Collection<ServerPlayer> var1, PlayerChatMessage var2) {
      ChatType.Bound var3 = ChatType.bind(ChatType.MSG_COMMAND_INCOMING, var0);
      OutgoingChatMessage var4 = OutgoingChatMessage.create(var2);
      boolean var5 = false;

      for(ServerPlayer var7 : var1) {
         ChatType.Bound var8 = ChatType.bind(ChatType.MSG_COMMAND_OUTGOING, var0).withTargetName(var7.getDisplayName());
         var0.sendChatMessage(var4, false, var8);
         boolean var9 = var0.shouldFilterMessageTo(var7);
         var7.sendChatMessage(var4, var9, var3);
         var5 |= var9 && var2.isFullyFiltered();
      }

      if (var5) {
         var0.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
      }
   }
}
