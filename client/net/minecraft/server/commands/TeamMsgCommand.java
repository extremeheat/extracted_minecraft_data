package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
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
         CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
         Entity var2 = var1.getEntityOrException();
         PlayerTeam var3 = var2.getTeam();
         if (var3 == null) {
            throw ERROR_NOT_ON_TEAM.create();
         } else {
            List var4 = var1.getServer().getPlayerList().getPlayers().stream().filter((var2x) -> {
               return var2x == var2 || var2x.getTeam() == var3;
            }).toList();
            if (!var4.isEmpty()) {
               MessageArgument.resolveChatMessage(var0x, "message", (var4x) -> {
                  sendMessage(var1, var2, var3, var4, var4x);
               });
            }

            return var4.size();
         }
      })));
      var0.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(var1));
   }

   private static void sendMessage(CommandSourceStack var0, Entity var1, PlayerTeam var2, List<ServerPlayer> var3, PlayerChatMessage var4) {
      MutableComponent var5 = var2.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
      ChatType.Bound var6 = ChatType.bind(ChatType.TEAM_MSG_COMMAND_INCOMING, var0).withTargetName(var5);
      ChatType.Bound var7 = ChatType.bind(ChatType.TEAM_MSG_COMMAND_OUTGOING, var0).withTargetName(var5);
      OutgoingChatMessage var8 = OutgoingChatMessage.create(var4);
      boolean var9 = false;

      boolean var13;
      for(Iterator var10 = var3.iterator(); var10.hasNext(); var9 |= var13 && var4.isFullyFiltered()) {
         ServerPlayer var11 = (ServerPlayer)var10.next();
         ChatType.Bound var12 = var11 == var1 ? var7 : var6;
         var13 = var0.shouldFilterMessageTo(var11);
         var11.sendChatMessage(var8, var13, var12);
      }

      if (var9) {
         var0.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
      }

   }

   static {
      SUGGEST_STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
      ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(Component.translatable("commands.teammsg.failed.noteam"));
   }
}
