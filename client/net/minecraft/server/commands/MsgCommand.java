package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;

public class MsgCommand {
   public MsgCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         MessageArgument.ChatMessage var1 = MessageArgument.getChatMessage(var0x, "message");

         try {
            return sendMessage((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), var1);
         } catch (Exception var3) {
            var1.consume((CommandSourceStack)var0x.getSource());
            throw var3;
         }
      }))));
      var0.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(var1));
      var0.register((LiteralArgumentBuilder)Commands.literal("w").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, Collection<ServerPlayer> var1, MessageArgument.ChatMessage var2) {
      ChatType.Bound var3 = ChatType.bind(ChatType.MSG_COMMAND_INCOMING, var0);
      var2.resolve(var0, (var3x) -> {
         OutgoingPlayerChatMessage var4 = OutgoingPlayerChatMessage.create(var3x);
         boolean var5 = var3x.isFullyFiltered();
         Entity var6 = var0.getEntity();
         boolean var7 = false;

         ServerPlayer var9;
         boolean var11;
         for(Iterator var8 = var1.iterator(); var8.hasNext(); var7 |= var5 && var11 && var9 != var6) {
            var9 = (ServerPlayer)var8.next();
            ChatType.Bound var10 = ChatType.bind(ChatType.MSG_COMMAND_OUTGOING, var0).withTargetName(var9.getDisplayName());
            var0.sendChatMessage(var4, false, var10);
            var11 = var0.shouldFilterMessageTo(var9);
            var9.sendChatMessage(var4, var11, var3);
         }

         if (var7) {
            var0.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
         }

         var4.sendHeadersToRemainingPlayers(var0.getServer().getPlayerList());
      });
      return var1.size();
   }
}
