package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("gamemode", GameModeArgument.gameMode())
                  .executes(var0x -> setMode((CommandSourceStack)var0x.getSource(), GameModeArgument.getGameMode(var0x, "gamemode")))
            )
      );
   }

   private static int setMode(CommandSourceStack var0, GameType var1) {
      int var2 = 0;
      MinecraftServer var3 = var0.getServer();
      var3.setDefaultGameType(var1);
      GameType var4 = var3.getForcedGameType();
      if (var4 != null) {
         for(ServerPlayer var6 : var3.getPlayerList().getPlayers()) {
            if (var6.setGameMode(var4)) {
               ++var2;
            }
         }
      }

      var0.sendSuccess(() -> Component.translatable("commands.defaultgamemode.success", var1.getLongDisplayName()), true);
      return var2;
   }
}
