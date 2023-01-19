package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final int MAX_CLONE_AREA = 32768;
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(Component.translatable("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatable("commands.clone.toobig", var0, var1)
   );
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.clone.failed"));
   public static final Predicate<BlockInWorld> FILTER_AIR = var0 -> !var0.getState().isAir();

   public CloneCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("begin", BlockPosArgument.blockPos())
                  .then(
                     Commands.argument("end", BlockPosArgument.blockPos())
                        .then(
                           ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                          "destination", BlockPosArgument.blockPos()
                                       )
                                       .executes(
                                          var0x -> clone(
                                                (CommandSourceStack)var0x.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                var0xx -> true,
                                                CloneCommands.Mode.NORMAL
                                             )
                                       ))
                                    .then(
                                       ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace")
                                                   .executes(
                                                      var0x -> clone(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                            var0xx -> true,
                                                            CloneCommands.Mode.NORMAL
                                                         )
                                                   ))
                                                .then(
                                                   Commands.literal("force")
                                                      .executes(
                                                         var0x -> clone(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                               var0xx -> true,
                                                               CloneCommands.Mode.FORCE
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("move")
                                                   .executes(
                                                      var0x -> clone(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                            var0xx -> true,
                                                            CloneCommands.Mode.MOVE
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("normal")
                                                .executes(
                                                   var0x -> clone(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                         var0xx -> true,
                                                         CloneCommands.Mode.NORMAL
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked")
                                                .executes(
                                                   var0x -> clone(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                         FILTER_AIR,
                                                         CloneCommands.Mode.NORMAL
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("force")
                                                   .executes(
                                                      var0x -> clone(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                            FILTER_AIR,
                                                            CloneCommands.Mode.FORCE
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("move")
                                                .executes(
                                                   var0x -> clone(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                         FILTER_AIR,
                                                         CloneCommands.Mode.MOVE
                                                      )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("normal")
                                             .executes(
                                                var0x -> clone(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                      BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                      BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                      FILTER_AIR,
                                                      CloneCommands.Mode.NORMAL
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("filtered")
                                    .then(
                                       ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                                      "filter", BlockPredicateArgument.blockPredicate(var1)
                                                   )
                                                   .executes(
                                                      var0x -> clone(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                            BlockPredicateArgument.getBlockPredicate(var0x, "filter"),
                                                            CloneCommands.Mode.NORMAL
                                                         )
                                                   ))
                                                .then(
                                                   Commands.literal("force")
                                                      .executes(
                                                         var0x -> clone(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                               BlockPredicateArgument.getBlockPredicate(var0x, "filter"),
                                                               CloneCommands.Mode.FORCE
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("move")
                                                   .executes(
                                                      var0x -> clone(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                            BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                            BlockPredicateArgument.getBlockPredicate(var0x, "filter"),
                                                            CloneCommands.Mode.MOVE
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("normal")
                                                .executes(
                                                   var0x -> clone(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "begin"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "end"),
                                                         BlockPosArgument.getLoadedBlockPos(var0x, "destination"),
                                                         BlockPredicateArgument.getBlockPredicate(var0x, "filter"),
                                                         CloneCommands.Mode.NORMAL
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int clone(CommandSourceStack var0, BlockPos var1, BlockPos var2, BlockPos var3, Predicate<BlockInWorld> var4, CloneCommands.Mode var5) throws CommandSyntaxException {
      BoundingBox var6 = BoundingBox.fromCorners(var1, var2);
      BlockPos var7 = var3.offset(var6.getLength());
      BoundingBox var8 = BoundingBox.fromCorners(var3, var7);
      if (!var5.canOverlap() && var8.intersects(var6)) {
         throw ERROR_OVERLAP.create();
      } else {
         int var9 = var6.getXSpan() * var6.getYSpan() * var6.getZSpan();
         if (var9 > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, var9);
         } else {
            ServerLevel var10 = var0.getLevel();
            if (var10.hasChunksAt(var1, var2) && var10.hasChunksAt(var3, var7)) {
               ArrayList var11 = Lists.newArrayList();
               ArrayList var12 = Lists.newArrayList();
               ArrayList var13 = Lists.newArrayList();
               LinkedList var14 = Lists.newLinkedList();
               BlockPos var15 = new BlockPos(var8.minX() - var6.minX(), var8.minY() - var6.minY(), var8.minZ() - var6.minZ());

               for(int var16 = var6.minZ(); var16 <= var6.maxZ(); ++var16) {
                  for(int var17 = var6.minY(); var17 <= var6.maxY(); ++var17) {
                     for(int var18 = var6.minX(); var18 <= var6.maxX(); ++var18) {
                        BlockPos var19 = new BlockPos(var18, var17, var16);
                        BlockPos var20 = var19.offset(var15);
                        BlockInWorld var21 = new BlockInWorld(var10, var19, false);
                        BlockState var22 = var21.getState();
                        if (var4.test(var21)) {
                           BlockEntity var23 = var10.getBlockEntity(var19);
                           if (var23 != null) {
                              CompoundTag var24 = var23.saveWithoutMetadata();
                              var12.add(new CloneCommands.CloneBlockInfo(var20, var22, var24));
                              var14.addLast(var19);
                           } else if (!var22.isSolidRender(var10, var19) && !var22.isCollisionShapeFullBlock(var10, var19)) {
                              var13.add(new CloneCommands.CloneBlockInfo(var20, var22, null));
                              var14.addFirst(var19);
                           } else {
                              var11.add(new CloneCommands.CloneBlockInfo(var20, var22, null));
                              var14.addLast(var19);
                           }
                        }
                     }
                  }
               }

               if (var5 == CloneCommands.Mode.MOVE) {
                  for(BlockPos var28 : var14) {
                     BlockEntity var31 = var10.getBlockEntity(var28);
                     Clearable.tryClear(var31);
                     var10.setBlock(var28, Blocks.BARRIER.defaultBlockState(), 2);
                  }

                  for(BlockPos var29 : var14) {
                     var10.setBlock(var29, Blocks.AIR.defaultBlockState(), 3);
                  }
               }

               ArrayList var27 = Lists.newArrayList();
               var27.addAll(var11);
               var27.addAll(var12);
               var27.addAll(var13);
               List var30 = Lists.reverse(var27);

               for(CloneCommands.CloneBlockInfo var34 : var30) {
                  BlockEntity var38 = var10.getBlockEntity(var34.pos);
                  Clearable.tryClear(var38);
                  var10.setBlock(var34.pos, Blocks.BARRIER.defaultBlockState(), 2);
               }

               int var33 = 0;

               for(CloneCommands.CloneBlockInfo var39 : var27) {
                  if (var10.setBlock(var39.pos, var39.state, 2)) {
                     ++var33;
                  }
               }

               for(CloneCommands.CloneBlockInfo var40 : var12) {
                  BlockEntity var42 = var10.getBlockEntity(var40.pos);
                  if (var40.tag != null && var42 != null) {
                     var42.load(var40.tag);
                     var42.setChanged();
                  }

                  var10.setBlock(var40.pos, var40.state, 2);
               }

               for(CloneCommands.CloneBlockInfo var41 : var30) {
                  var10.blockUpdated(var41.pos, var41.state.getBlock());
               }

               var10.getBlockTicks().copyArea(var6, var15);
               if (var33 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  var0.sendSuccess(Component.translatable("commands.clone.success", var33), true);
                  return var33;
               }
            } else {
               throw BlockPosArgument.ERROR_NOT_LOADED.create();
            }
         }
      }
   }

   static class CloneBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundTag tag;

      public CloneBlockInfo(BlockPos var1, BlockState var2, @Nullable CompoundTag var3) {
         super();
         this.pos = var1;
         this.state = var2;
         this.tag = var3;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(boolean var3) {
         this.canOverlap = var3;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }
   }
}
