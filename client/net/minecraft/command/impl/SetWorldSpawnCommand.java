package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class SetWorldSpawnCommand {
   public static void func_198702_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("setworldspawn").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).executes((var0x) -> {
         return func_198701_a((CommandSource)var0x.getSource(), new BlockPos(((CommandSource)var0x.getSource()).func_197036_d()));
      })).then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a()).executes((var0x) -> {
         return func_198701_a((CommandSource)var0x.getSource(), BlockPosArgument.func_197274_b(var0x, "pos"));
      })));
   }

   private static int func_198701_a(CommandSource var0, BlockPos var1) {
      var0.func_197023_e().func_175652_B(var1);
      var0.func_197028_i().func_184103_al().func_148540_a(new SPacketSpawnPosition(var1));
      var0.func_197030_a(new TextComponentTranslation("commands.setworldspawn.success", new Object[]{var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p()}), true);
      return 1;
   }
}
