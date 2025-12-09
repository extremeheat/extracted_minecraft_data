package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public DefaultGameModeCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes((var0x) -> setMode((CommandSourceStack)var0x.getSource(), GameModeArgument.getGameMode(var0x, "gamemode")))));
   }

   private static int setMode(CommandSourceStack var0, GameType var1) {
      MinecraftServer var2 = var0.getServer();
      var2.setDefaultGameType(var1);
      int var3 = var2.enforceGameTypeForPlayers(var2.getForcedGameType());
      var0.sendSuccess(() -> Component.translatable("commands.defaultgamemode.success", var1.getLongDisplayName()), true);
      return var3;
   }
}
