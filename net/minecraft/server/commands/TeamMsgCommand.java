package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(new TranslatableComponent("commands.teammsg.failed.noteam", new Object[0]));

   public static void register(CommandDispatcher var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((var0x) -> {
         return sendMessage((CommandSourceStack)var0x.getSource(), MessageArgument.getMessage(var0x, "message"));
      })));
      var0.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack var0, Component var1) throws CommandSyntaxException {
      Entity var2 = var0.getEntityOrException();
      PlayerTeam var3 = (PlayerTeam)var2.getTeam();
      if (var3 == null) {
         throw ERROR_NOT_ON_TEAM.create();
      } else {
         Consumer var4 = (var0x) -> {
            var0x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
         };
         Component var5 = var3.getFormattedDisplayName().withStyle(var4);
         Iterator var6 = var5.getSiblings().iterator();

         while(var6.hasNext()) {
            Component var7 = (Component)var6.next();
            var7.withStyle(var4);
         }

         List var9 = var0.getServer().getPlayerList().getPlayers();
         Iterator var10 = var9.iterator();

         while(var10.hasNext()) {
            ServerPlayer var8 = (ServerPlayer)var10.next();
            if (var8 == var2) {
               var8.sendMessage(new TranslatableComponent("chat.type.team.sent", new Object[]{var5, var0.getDisplayName(), var1.deepCopy()}));
            } else if (var8.getTeam() == var3) {
               var8.sendMessage(new TranslatableComponent("chat.type.team.text", new Object[]{var5, var0.getDisplayName(), var1.deepCopy()}));
            }
         }

         return var9.size();
      }
   }
}
