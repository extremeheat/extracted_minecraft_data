package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;

public class DebugPathCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_MOB = new SimpleCommandExceptionType(Component.literal("Source is not a mob"));
   private static final SimpleCommandExceptionType ERROR_NO_PATH = new SimpleCommandExceptionType(Component.literal("Path not found"));
   private static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.literal("Target not reached"));

   public DebugPathCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debugpath").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("to", BlockPosArgument.blockPos()).executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "to"));
      })));
   }

   private static int fillBlocks(CommandSourceStack var0, BlockPos var1) throws CommandSyntaxException {
      Entity var2 = var0.getEntity();
      if (!(var2 instanceof Mob var3)) {
         throw ERROR_NOT_MOB.create();
      } else {
         GroundPathNavigation var4 = new GroundPathNavigation(var3, var0.getLevel());
         Path var5 = ((PathNavigation)var4).createPath((BlockPos)var1, 0);
         DebugPackets.sendPathFindingPacket(var0.getLevel(), var3, var5, ((PathNavigation)var4).getMaxDistanceToWaypoint());
         if (var5 == null) {
            throw ERROR_NO_PATH.create();
         } else if (!var5.canReach()) {
            throw ERROR_NOT_COMPLETE.create();
         } else {
            var0.sendSuccess(() -> {
               return Component.literal("Made path");
            }, true);
            return 1;
         }
      }
   }
}
