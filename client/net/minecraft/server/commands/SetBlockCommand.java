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
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.setblock.failed"));

   public SetBlockCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("pos", BlockPosArgument.blockPos())
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block(var1))
                                 .executes(
                                    var0x -> setBlock(
                                          (CommandSourceStack)var0x.getSource(),
                                          BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                          BlockStateArgument.getBlock(var0x, "block"),
                                          SetBlockCommand.Mode.REPLACE,
                                          null
                                       )
                                 ))
                              .then(
                                 Commands.literal("destroy")
                                    .executes(
                                       var0x -> setBlock(
                                             (CommandSourceStack)var0x.getSource(),
                                             BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                             BlockStateArgument.getBlock(var0x, "block"),
                                             SetBlockCommand.Mode.DESTROY,
                                             null
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("keep")
                                 .executes(
                                    var0x -> setBlock(
                                          (CommandSourceStack)var0x.getSource(),
                                          BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                          BlockStateArgument.getBlock(var0x, "block"),
                                          SetBlockCommand.Mode.REPLACE,
                                          var0xx -> var0xx.getLevel().isEmptyBlock(var0xx.getPos())
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("replace")
                              .executes(
                                 var0x -> setBlock(
                                       (CommandSourceStack)var0x.getSource(),
                                       BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                       BlockStateArgument.getBlock(var0x, "block"),
                                       SetBlockCommand.Mode.REPLACE,
                                       null
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int setBlock(CommandSourceStack var0, BlockPos var1, BlockInput var2, SetBlockCommand.Mode var3, @Nullable Predicate<BlockInWorld> var4) throws CommandSyntaxException {
      ServerLevel var5 = var0.getLevel();
      if (var4 != null && !var4.test(new BlockInWorld(var5, var1, true))) {
         throw ERROR_FAILED.create();
      } else {
         boolean var6;
         if (var3 == SetBlockCommand.Mode.DESTROY) {
            var5.destroyBlock(var1, true);
            var6 = !var2.getState().isAir() || !var5.getBlockState(var1).isAir();
         } else {
            BlockEntity var7 = var5.getBlockEntity(var1);
            Clearable.tryClear(var7);
            var6 = true;
         }

         if (var6 && !var2.place(var5, var1, 2)) {
            throw ERROR_FAILED.create();
         } else {
            var5.blockUpdated(var1, var2.getState().getBlock());
            var0.sendSuccess(() -> Component.translatable("commands.setblock.success", var1.getX(), var1.getY(), var1.getZ()), true);
            return 1;
         }
      }
   }

   public interface Filter {
      @Nullable
      BlockInput filter(BoundingBox var1, BlockPos var2, BlockInput var3, ServerLevel var4);
   }

   public static enum Mode {
      REPLACE,
      DESTROY;

      private Mode() {
      }
   }
}
