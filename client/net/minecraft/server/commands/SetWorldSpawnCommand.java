package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;

public class SetWorldSpawnCommand {
   public SetWorldSpawnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), WorldCoordinates.ZERO_ROTATION))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), BlockPosArgument.getSpawnablePos(var0x, "pos"), WorldCoordinates.ZERO_ROTATION))).then(Commands.argument("rotation", RotationArgument.rotation()).executes((var0x) -> setSpawn((CommandSourceStack)var0x.getSource(), BlockPosArgument.getSpawnablePos(var0x, "pos"), RotationArgument.getRotation(var0x, "rotation"))))));
   }

   private static int setSpawn(CommandSourceStack var0, BlockPos var1, Coordinates var2) {
      ServerLevel var3 = var0.getLevel();
      Vec2 var4 = var2.getRotation(var0);
      float var5 = var4.y;
      float var6 = var4.x;
      var3.setRespawnData(LevelData.RespawnData.of(var3.dimension(), var1, var5, var6));
      var0.sendSuccess(() -> Component.translatable("commands.setworldspawn.success", var1.getX(), var1.getY(), var1.getZ(), var5, var6, var3.dimension().location().toString()), true);
      return 1;
   }
}
