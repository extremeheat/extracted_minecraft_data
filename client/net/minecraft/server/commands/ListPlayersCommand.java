package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class ListPlayersCommand {
   public ListPlayersCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((var0x) -> {
         return listPlayers((CommandSourceStack)var0x.getSource());
      })).then(Commands.literal("uuids").executes((var0x) -> {
         return listPlayersWithUuids((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int listPlayers(CommandSourceStack var0) {
      return format(var0, Player::getDisplayName);
   }

   private static int listPlayersWithUuids(CommandSourceStack var0) {
      return format(var0, (var0x) -> {
         return Component.translatable("commands.list.nameAndId", var0x.getName(), Component.translationArg(var0x.getGameProfile().getId()));
      });
   }

   private static int format(CommandSourceStack var0, Function<ServerPlayer, Component> var1) {
      PlayerList var2 = var0.getServer().getPlayerList();
      List var3 = var2.getPlayers();
      Component var4 = ComponentUtils.formatList(var3, (Function)var1);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.list.players", var3.size(), var2.getMaxPlayers(), var4);
      }, false);
      return var3.size();
   }
}
