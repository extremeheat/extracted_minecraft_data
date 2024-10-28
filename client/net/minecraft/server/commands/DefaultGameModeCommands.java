package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public DefaultGameModeCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes((var0x) -> {
         return setMode((CommandSourceStack)var0x.getSource(), GameModeArgument.getGameMode(var0x, "gamemode"));
      })));
   }

   private static int setMode(CommandSourceStack var0, GameType var1) {
      int var2 = 0;
      MinecraftServer var3 = var0.getServer();
      var3.setDefaultGameType(var1);
      GameType var4 = var3.getForcedGameType();
      if (var4 != null) {
         Iterator var5 = var3.getPlayerList().getPlayers().iterator();

         while(var5.hasNext()) {
            ServerPlayer var6 = (ServerPlayer)var5.next();
            if (var6.setGameMode(var4)) {
               ++var2;
            }
         }
      }

      var0.sendSuccess(() -> {
         return Component.translatable("commands.defaultgamemode.success", var1.getLongDisplayName());
      }, true);
      return var2;
   }
}
