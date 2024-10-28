package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static final int PERMISSION_LEVEL = 2;

   public GameModeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("gamemode").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("gamemode", GameModeArgument.gameMode()).executes((var0x) -> {
         return setMode(var0x, Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), GameModeArgument.getGameMode(var0x, "gamemode"));
      })).then(Commands.argument("target", EntityArgument.players()).executes((var0x) -> {
         return setMode(var0x, EntityArgument.getPlayers(var0x, "target"), GameModeArgument.getGameMode(var0x, "gamemode"));
      }))));
   }

   private static void logGamemodeChange(CommandSourceStack var0, ServerPlayer var1, GameType var2) {
      MutableComponent var3 = Component.translatable("gameMode." + var2.getName());
      if (var0.getEntity() == var1) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.gamemode.success.self", var3);
         }, true);
      } else {
         if (var0.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            var1.sendSystemMessage(Component.translatable("gameMode.changed", var3));
         }

         var0.sendSuccess(() -> {
            return Component.translatable("commands.gamemode.success.other", var1.getDisplayName(), var3);
         }, true);
      }

   }

   private static int setMode(CommandContext<CommandSourceStack> var0, Collection<ServerPlayer> var1, GameType var2) {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (var5.setGameMode(var2)) {
            logGamemodeChange((CommandSourceStack)var0.getSource(), var5, var2);
            ++var3;
         }
      }

      return var3;
   }
}
