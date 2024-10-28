package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public class SetSpawnCommand {
   public SetSpawnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPosArgument.getSpawnablePos(var0x, "pos"), 0.0F);
      })).then(Commands.argument("angle", AngleArgument.angle()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPosArgument.getSpawnablePos(var0x, "pos"), AngleArgument.getAngle(var0x, "angle"));
      })))));
   }

   private static int setSpawn(CommandSourceStack var0, Collection<ServerPlayer> var1, BlockPos var2, float var3) {
      ResourceKey var4 = var0.getLevel().dimension();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ServerPlayer var6 = (ServerPlayer)var5.next();
         var6.setRespawnPosition(var4, var2, var3, true, false);
      }

      String var7 = var4.location().toString();
      if (var1.size() == 1) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.spawnpoint.success.single", var2.getX(), var2.getY(), var2.getZ(), var3, var7, ((ServerPlayer)var1.iterator().next()).getDisplayName());
         }, true);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.spawnpoint.success.multiple", var2.getX(), var2.getY(), var2.getZ(), var3, var7, var1.size());
         }, true);
      }

      return var1.size();
   }
}
