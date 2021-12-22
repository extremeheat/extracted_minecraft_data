package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static final int PERMISSION_LEVEL = 2;

   public GameModeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("gamemode").requires((var0x) -> {
         return var0x.hasPermission(2);
      });
      GameType[] var2 = GameType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         var1.then(((LiteralArgumentBuilder)Commands.literal(var5.getName()).executes((var1x) -> {
            return setMode(var1x, Collections.singleton(((CommandSourceStack)var1x.getSource()).getPlayerOrException()), var5);
         })).then(Commands.argument("target", EntityArgument.players()).executes((var1x) -> {
            return setMode(var1x, EntityArgument.getPlayers(var1x, "target"), var5);
         })));
      }

      var0.register(var1);
   }

   private static void logGamemodeChange(CommandSourceStack var0, ServerPlayer var1, GameType var2) {
      TranslatableComponent var3 = new TranslatableComponent("gameMode." + var2.getName());
      if (var0.getEntity() == var1) {
         var0.sendSuccess(new TranslatableComponent("commands.gamemode.success.self", new Object[]{var3}), true);
      } else {
         if (var0.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            var1.sendMessage(new TranslatableComponent("gameMode.changed", new Object[]{var3}), Util.NIL_UUID);
         }

         var0.sendSuccess(new TranslatableComponent("commands.gamemode.success.other", new Object[]{var1.getDisplayName(), var3}), true);
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
