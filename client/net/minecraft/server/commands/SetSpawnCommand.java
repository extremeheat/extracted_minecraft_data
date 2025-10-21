package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;

public class SetSpawnCommand {
   public SetSpawnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), WorldCoordinates.ZERO_ROTATION))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), WorldCoordinates.ZERO_ROTATION))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPosArgument.getSpawnablePos(var0x, "pos"), WorldCoordinates.ZERO_ROTATION))).then(Commands.argument("rotation", RotationArgument.rotation()).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), BlockPosArgument.getSpawnablePos(var0x, "pos"), RotationArgument.getRotation(var0x, "rotation")))))));
   }

   private static int setSpawn(CommandSourceStack var0, Collection<ServerPlayer> var1, BlockPos var2, Coordinates var3) {
      ResourceKey var4 = var0.getLevel().dimension();
      Vec2 var5 = var3.getRotation(var0);
      float var6 = Mth.wrapDegrees(var5.y);
      float var7 = Mth.clamp(var5.x, -90.0F, 90.0F);

      for(ServerPlayer var9 : var1) {
         var9.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(var4, var2, var6, var7), true), false);
      }

      String var10 = var4.location().toString();
      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.spawnpoint.success.single", var2.getX(), var2.getY(), var2.getZ(), var6, var7, var10, ((ServerPlayer)var1.iterator().next()).getDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.spawnpoint.success.multiple", var2.getX(), var2.getY(), var2.getZ(), var6, var7, var10, var1.size()), true);
      }

      return var1.size();
   }
}
