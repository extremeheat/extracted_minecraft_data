package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;

public class SetWorldSpawnCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), new BlockPos(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return setSpawn((CommandSourceStack)var0x.getSource(), BlockPosArgument.getOrLoadBlockPos(var0x, "pos"));
      })));
   }

   private static int setSpawn(CommandSourceStack var0, BlockPos var1) {
      var0.getLevel().setSpawnPos(var1);
      var0.getServer().getPlayerList().broadcastAll(new ClientboundSetSpawnPositionPacket(var1));
      var0.sendSuccess(new TranslatableComponent("commands.setworldspawn.success", new Object[]{var1.getX(), var1.getY(), var1.getZ()}), true);
      return 1;
   }
}
