package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class SetSpawnCommand {
   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), new BlockPos(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), new BlockPos(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPosArgument.getOrLoadBlockPos(var0x, "pos"));
      }))));
   }

   private static int setSpawn(CommandSourceStack var0, Collection var1, BlockPos var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         var4.setRespawnPosition(var2, true, false);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.single", new Object[]{var2.getX(), var2.getY(), var2.getZ(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.multiple", new Object[]{var2.getX(), var2.getY(), var2.getZ(), var1.size()}), true);
      }

      return var1.size();
   }
}
