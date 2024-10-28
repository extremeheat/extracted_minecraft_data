package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class SetWorldSpawnCommand {
   public SetWorldSpawnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), BlockPosArgument.getSpawnablePos(var0x, "pos"), 0.0F);
      })).then(Commands.argument("angle", AngleArgument.angle()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), BlockPosArgument.getSpawnablePos(var0x, "pos"), AngleArgument.getAngle(var0x, "angle"));
      }))));
   }

   private static int setSpawn(CommandSourceStack var0, BlockPos var1, float var2) {
      ServerLevel var3 = var0.getLevel();
      if (var3.dimension() != Level.OVERWORLD) {
         var0.sendFailure(Component.translatable("commands.setworldspawn.failure.not_overworld"));
         return 0;
      } else {
         var3.setDefaultSpawnPos(var1, var2);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.setworldspawn.success", var1.getX(), var1.getY(), var1.getZ(), var2);
         }, true);
         return 1;
      }
   }
}
