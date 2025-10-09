package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static final PermissionCheck PERMISSION_CHECK;

   public GameModeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("gamemode").requires(Commands.hasPermission(PERMISSION_CHECK))).then(((RequiredArgumentBuilder)Commands.argument("gamemode", GameModeArgument.gameMode()).executes((var0x) -> setMode(var0x, Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), GameModeArgument.getGameMode(var0x, "gamemode")))).then(Commands.argument("target", EntityArgument.players()).executes((var0x) -> setMode(var0x, EntityArgument.getPlayers(var0x, "target"), GameModeArgument.getGameMode(var0x, "gamemode"))))));
   }

   private static void logGamemodeChange(CommandSourceStack var0, ServerPlayer var1, GameType var2) {
      MutableComponent var3 = Component.translatable("gameMode." + var2.getName());
      if (var0.getEntity() == var1) {
         var0.sendSuccess(() -> Component.translatable("commands.gamemode.success.self", var3), true);
      } else {
         if (var0.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            var1.sendSystemMessage(Component.translatable("gameMode.changed", var3));
         }

         var0.sendSuccess(() -> Component.translatable("commands.gamemode.success.other", var1.getDisplayName(), var3), true);
      }

   }

   private static int setMode(CommandContext<CommandSourceStack> var0, Collection<ServerPlayer> var1, GameType var2) {
      int var3 = 0;

      for(ServerPlayer var5 : var1) {
         if (setGameMode((CommandSourceStack)var0.getSource(), var5, var2)) {
            ++var3;
         }
      }

      return var3;
   }

   public static void setGameMode(ServerPlayer var0, GameType var1) {
      setGameMode(var0.createCommandSourceStack(), var0, var1);
   }

   private static boolean setGameMode(CommandSourceStack var0, ServerPlayer var1, GameType var2) {
      if (var1.setGameMode(var2)) {
         logGamemodeChange(var0, var1, var2);
         return true;
      } else {
         return false;
      }
   }

   static {
      PERMISSION_CHECK = new PermissionCheck.Require(Permissions.COMMANDS_GAMEMASTER);
   }
}
