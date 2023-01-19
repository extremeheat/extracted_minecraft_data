package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public DefaultGameModeCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires(var0x -> var0x.hasPermission(2));

      for(GameType var5 : GameType.values()) {
         var1.then(Commands.literal(var5.getName()).executes(var1x -> setMode((CommandSourceStack)var1x.getSource(), var5)));
      }

      var0.register(var1);
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

      var0.sendSuccess(Component.translatable("commands.defaultgamemode.success", var1.getLongDisplayName()), true);
      return var2;
   }
}
