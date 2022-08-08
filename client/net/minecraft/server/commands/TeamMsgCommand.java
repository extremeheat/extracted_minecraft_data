package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final Style SUGGEST_STYLE;
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM;

   public TeamMsgCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         MessageArgument.ChatMessage var1 = MessageArgument.getChatMessage(var0x, "message");

         try {
            return sendMessage((CommandSourceStack)var0x.getSource(), var1);
         } catch (Exception var3) {
            var1.consume((CommandSourceStack)var0x.getSource());
            throw var3;
         }
      })));
      var0.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, MessageArgument.ChatMessage var1) throws CommandSyntaxException {
      Entity var2 = var0.getEntityOrException();
      PlayerTeam var3 = (PlayerTeam)var2.getTeam();
      if (var3 == null) {
         throw ERROR_NOT_ON_TEAM.create();
      } else {
         MutableComponent var4 = var3.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
         ChatType.Bound var5 = ChatType.bind(ChatType.TEAM_MSG_COMMAND_INCOMING, var0).withTargetName(var4);
         ChatType.Bound var6 = ChatType.bind(ChatType.TEAM_MSG_COMMAND_OUTGOING, var0).withTargetName(var4);
         List var7 = var0.getServer().getPlayerList().getPlayers().stream().filter((var2x) -> {
            return var2x == var2 || var2x.getTeam() == var3;
         }).toList();
         var1.resolve(var0, (var5x) -> {
            OutgoingPlayerChatMessage var6x = OutgoingPlayerChatMessage.create(var5x);
            boolean var7x = var5x.isFullyFiltered();
            boolean var8 = false;

            ServerPlayer var10;
            boolean var12;
            for(Iterator var9 = var7.iterator(); var9.hasNext(); var8 |= var7x && var12 && var10 != var2) {
               var10 = (ServerPlayer)var9.next();
               ChatType.Bound var11 = var10 == var2 ? var6 : var5;
               var12 = var0.shouldFilterMessageTo(var10);
               var10.sendChatMessage(var6x, var12, var11);
            }

            if (var8) {
               var0.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
            }

            var6x.sendHeadersToRemainingPlayers(var0.getServer().getPlayerList());
         });
         return var7.size();
      }
   }

   static {
      SUGGEST_STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
      ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(Component.translatable("commands.teammsg.failed.noteam"));
   }
}
