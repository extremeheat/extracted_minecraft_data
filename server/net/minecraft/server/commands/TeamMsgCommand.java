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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final Style SUGGEST_STYLE;
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM;

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
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
         MutableComponent var4 = var3.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
         List var5 = var0.getServer().getPlayerList().getPlayers();
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            ServerPlayer var7 = (ServerPlayer)var6.next();
            if (var7 == var2) {
               var7.sendMessage(new TranslatableComponent("chat.type.team.sent", new Object[]{var4, var0.getDisplayName(), var1}), var2.getUUID());
            } else if (var7.getTeam() == var3) {
               var7.sendMessage(new TranslatableComponent("chat.type.team.text", new Object[]{var4, var0.getDisplayName(), var1}), var2.getUUID());
            }
         }

         return var5.size();
      }
   }

   static {
      SUGGEST_STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
      ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(new TranslatableComponent("commands.teammsg.failed.noteam"));
   }
}
