package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final Style SUGGEST_STYLE = Style.EMPTY
      .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.type.team.hover")))
      .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(Component.translatable("commands.teammsg.failed.noteam"));

   public TeamMsgCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register(
         (LiteralArgumentBuilder)Commands.literal("teammsg")
            .then(
               Commands.argument("message", MessageArgument.message())
                  .executes(var0x -> sendMessage((CommandSourceStack)var0x.getSource(), MessageArgument.getChatMessage(var0x, "message")))
            )
      );
      var0.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, MessageArgument.ChatMessage var1) throws CommandSyntaxException {
      Entity var2 = var0.getEntityOrException();
      PlayerTeam var3 = (PlayerTeam)var2.getTeam();
      if (var3 == null) {
         throw ERROR_NOT_ON_TEAM.create();
      } else {
         MutableComponent var4 = var3.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
         ChatSender var5 = var0.asChatSender().withTeamName(var4);
         List var6 = var0.getServer().getPlayerList().getPlayers().stream().filter(var2x -> var2x == var2 || var2x.getTeam() == var3).toList();
         if (var6.isEmpty()) {
            return 0;
         } else {
            var1.resolve(var0).thenAcceptAsync(var5x -> {
               for(ServerPlayer var7 : var6) {
                  if (var7 == var2) {
                     var7.sendSystemMessage(Component.translatable("chat.type.team.sent", var4, var0.getDisplayName(), var5x.raw().serverContent()));
                  } else {
                     PlayerChatMessage var8 = var5x.filter(var0, var7);
                     if (var8 != null) {
                        var7.sendChatMessage(var8, var5, ChatType.TEAM_MSG_COMMAND);
                     }
                  }
               }
            }, var0.getServer());
            return var6.size();
         }
      }
   }
}
