package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.setblock.failed"));

   public SetBlockCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      Predicate var2 = (var0x) -> var0x.getLevel().isEmptyBlock(var0x.getPos());
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block(var1)).executes((var0x) -> setBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), BlockStateArgument.getBlock(var0x, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null, false))).then(Commands.literal("destroy").executes((var0x) -> setBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), BlockStateArgument.getBlock(var0x, "block"), SetBlockCommand.Mode.DESTROY, (Predicate)null, false)))).then(Commands.literal("keep").executes((var1x) -> setBlock((CommandSourceStack)var1x.getSource(), BlockPosArgument.getLoadedBlockPos(var1x, "pos"), BlockStateArgument.getBlock(var1x, "block"), SetBlockCommand.Mode.REPLACE, var2, false)))).then(Commands.literal("replace").executes((var0x) -> setBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), BlockStateArgument.getBlock(var0x, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null, false)))).then(Commands.literal("strict").executes((var0x) -> setBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), BlockStateArgument.getBlock(var0x, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null, true))))));
   }

   private static int setBlock(CommandSourceStack var0, BlockPos var1, BlockInput var2, Mode var3, @Nullable Predicate<BlockInWorld> var4, boolean var5) throws CommandSyntaxException {
      ServerLevel var6 = var0.getLevel();
      if (var6.isDebug()) {
         throw ERROR_FAILED.create();
      } else if (var4 != null && !var4.test(new BlockInWorld(var6, var1, true))) {
         throw ERROR_FAILED.create();
      } else {
         boolean var7;
         if (var3 == SetBlockCommand.Mode.DESTROY) {
            var6.destroyBlock(var1, true);
            var7 = !var2.getState().isAir() || !var6.getBlockState(var1).isAir();
         } else {
            var7 = true;
         }

         if (var7 && !var2.place(var6, var1, 2 | (var5 ? 304 : 256))) {
            throw ERROR_FAILED.create();
         } else {
            if (!var5) {
               var6.updateNeighborsAt(var1, var2.getState().getBlock());
            }

            var0.sendSuccess(() -> Component.translatable("commands.setblock.success", var1.getX(), var1.getY(), var1.getZ()), true);
            return 1;
         }
      }
   }

   public static enum Mode {
      REPLACE,
      DESTROY;

      private Mode() {
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{REPLACE, DESTROY};
      }
   }
}
