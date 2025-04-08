package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(Component.translatable("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.clone.toobig", var0, var1));
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.clone.failed"));
   public static final Predicate<BlockInWorld> FILTER_AIR = (var0) -> !var0.getState().isAir();

   public CloneCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires((var0x) -> var0x.hasPermission(2))).then(beginEndDestinationAndModeSuffix(var1, (var0x) -> ((CommandSourceStack)var0x.getSource()).getLevel()))).then(Commands.literal("from").then(Commands.argument("sourceDimension", DimensionArgument.dimension()).then(beginEndDestinationAndModeSuffix(var1, (var0x) -> DimensionArgument.getDimension(var0x, "sourceDimension"))))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> beginEndDestinationAndModeSuffix(CommandBuildContext var0, InCommandFunction<CommandContext<CommandSourceStack>, ServerLevel> var1) {
      return Commands.argument("begin", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("end", BlockPosArgument.blockPos()).then(destinationAndStrictSuffix(var0, var1, (var0x) -> ((CommandSourceStack)var0x.getSource()).getLevel()))).then(Commands.literal("to").then(Commands.argument("targetDimension", DimensionArgument.dimension()).then(destinationAndStrictSuffix(var0, var1, (var0x) -> DimensionArgument.getDimension(var0x, "targetDimension"))))));
   }

   private static DimensionAndPosition getLoadedDimensionAndPosition(CommandContext<CommandSourceStack> var0, ServerLevel var1, String var2) throws CommandSyntaxException {
      BlockPos var3 = BlockPosArgument.getLoadedBlockPos(var0, var1, var2);
      return new DimensionAndPosition(var1, var3);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> destinationAndStrictSuffix(CommandBuildContext var0, InCommandFunction<CommandContext<CommandSourceStack>, ServerLevel> var1, InCommandFunction<CommandContext<CommandSourceStack>, ServerLevel> var2) {
      InCommandFunction var3 = (var1x) -> getLoadedDimensionAndPosition(var1x, (ServerLevel)var1.apply(var1x), "begin");
      InCommandFunction var4 = (var1x) -> getLoadedDimensionAndPosition(var1x, (ServerLevel)var1.apply(var1x), "end");
      InCommandFunction var5 = (var1x) -> getLoadedDimensionAndPosition(var1x, (ServerLevel)var2.apply(var1x), "destination");
      return modeSuffix(var0, var3, var4, var5, false, Commands.argument("destination", BlockPosArgument.blockPos())).then(modeSuffix(var0, var3, var4, var5, true, Commands.literal("strict")));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> modeSuffix(CommandBuildContext var0, InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var1, InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var2, InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var3, boolean var4, ArgumentBuilder<CommandSourceStack, ?> var5) {
      return var5.executes((var4x) -> clone((CommandSourceStack)var4x.getSource(), (DimensionAndPosition)var1.apply(var4x), (DimensionAndPosition)var2.apply(var4x), (DimensionAndPosition)var3.apply(var4x), (var0) -> true, CloneCommands.Mode.NORMAL, var4)).then(wrapWithCloneMode(var1, var2, var3, (var0x) -> (var0) -> true, var4, Commands.literal("replace"))).then(wrapWithCloneMode(var1, var2, var3, (var0x) -> FILTER_AIR, var4, Commands.literal("masked"))).then(Commands.literal("filtered").then(wrapWithCloneMode(var1, var2, var3, (var0x) -> BlockPredicateArgument.getBlockPredicate(var0x, "filter"), var4, Commands.argument("filter", BlockPredicateArgument.blockPredicate(var0)))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapWithCloneMode(InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var0, InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var1, InCommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> var2, InCommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> var3, boolean var4, ArgumentBuilder<CommandSourceStack, ?> var5) {
      return var5.executes((var5x) -> clone((CommandSourceStack)var5x.getSource(), (DimensionAndPosition)var0.apply(var5x), (DimensionAndPosition)var1.apply(var5x), (DimensionAndPosition)var2.apply(var5x), (Predicate)var3.apply(var5x), CloneCommands.Mode.NORMAL, var4)).then(Commands.literal("force").executes((var5x) -> clone((CommandSourceStack)var5x.getSource(), (DimensionAndPosition)var0.apply(var5x), (DimensionAndPosition)var1.apply(var5x), (DimensionAndPosition)var2.apply(var5x), (Predicate)var3.apply(var5x), CloneCommands.Mode.FORCE, var4))).then(Commands.literal("move").executes((var5x) -> clone((CommandSourceStack)var5x.getSource(), (DimensionAndPosition)var0.apply(var5x), (DimensionAndPosition)var1.apply(var5x), (DimensionAndPosition)var2.apply(var5x), (Predicate)var3.apply(var5x), CloneCommands.Mode.MOVE, var4))).then(Commands.literal("normal").executes((var5x) -> clone((CommandSourceStack)var5x.getSource(), (DimensionAndPosition)var0.apply(var5x), (DimensionAndPosition)var1.apply(var5x), (DimensionAndPosition)var2.apply(var5x), (Predicate)var3.apply(var5x), CloneCommands.Mode.NORMAL, var4)));
   }

   private static int clone(CommandSourceStack var0, DimensionAndPosition var1, DimensionAndPosition var2, DimensionAndPosition var3, Predicate<BlockInWorld> var4, Mode var5, boolean var6) throws CommandSyntaxException {
      BlockPos var7 = var1.position();
      BlockPos var8 = var2.position();
      BoundingBox var9 = BoundingBox.fromCorners(var7, var8);
      BlockPos var10 = var3.position();
      BlockPos var11 = var10.offset(var9.getLength());
      BoundingBox var12 = BoundingBox.fromCorners(var10, var11);
      ServerLevel var13 = var1.dimension();
      ServerLevel var14 = var3.dimension();
      if (!var5.canOverlap() && var13 == var14 && var12.intersects(var9)) {
         throw ERROR_OVERLAP.create();
      } else {
         int var15 = var9.getXSpan() * var9.getYSpan() * var9.getZSpan();
         int var16 = var0.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
         if (var15 > var16) {
            throw ERROR_AREA_TOO_LARGE.create(var16, var15);
         } else if (var13.hasChunksAt(var7, var8) && var14.hasChunksAt(var10, var11)) {
            if (var14.isDebug()) {
               throw ERROR_FAILED.create();
            } else {
               ArrayList var17 = Lists.newArrayList();
               ArrayList var18 = Lists.newArrayList();
               ArrayList var19 = Lists.newArrayList();
               LinkedList var20 = Lists.newLinkedList();
               BlockPos var21 = new BlockPos(var12.minX() - var9.minX(), var12.minY() - var9.minY(), var12.minZ() - var9.minZ());

               for(int var22 = var9.minZ(); var22 <= var9.maxZ(); ++var22) {
                  for(int var23 = var9.minY(); var23 <= var9.maxY(); ++var23) {
                     for(int var24 = var9.minX(); var24 <= var9.maxX(); ++var24) {
                        BlockPos var25 = new BlockPos(var24, var23, var22);
                        BlockPos var26 = var25.offset(var21);
                        BlockInWorld var27 = new BlockInWorld(var13, var25, false);
                        BlockState var28 = var27.getState();
                        if (var4.test(var27)) {
                           BlockEntity var29 = var13.getBlockEntity(var25);
                           if (var29 != null) {
                              CloneBlockEntityInfo var30 = new CloneBlockEntityInfo(var29.saveCustomOnly(var0.registryAccess()), var29.components());
                              var18.add(new CloneBlockInfo(var26, var28, var30, var14.getBlockState(var26)));
                              var20.addLast(var25);
                           } else if (!var28.isSolidRender() && !var28.isCollisionShapeFullBlock(var13, var25)) {
                              var19.add(new CloneBlockInfo(var26, var28, (CloneBlockEntityInfo)null, var14.getBlockState(var26)));
                              var20.addFirst(var25);
                           } else {
                              var17.add(new CloneBlockInfo(var26, var28, (CloneBlockEntityInfo)null, var14.getBlockState(var26)));
                              var20.addLast(var25);
                           }
                        }
                     }
                  }
               }

               int var31 = 2 | (var6 ? 816 : 0);
               if (var5 == CloneCommands.Mode.MOVE) {
                  for(BlockPos var35 : var20) {
                     var13.setBlock(var35, Blocks.BARRIER.defaultBlockState(), var31 | 816);
                  }

                  int var33 = var6 ? var31 : 3;

                  for(BlockPos var38 : var20) {
                     var13.setBlock(var38, Blocks.AIR.defaultBlockState(), var33);
                  }
               }

               ArrayList var34 = Lists.newArrayList();
               var34.addAll(var17);
               var34.addAll(var18);
               var34.addAll(var19);
               List var37 = Lists.reverse(var34);

               for(CloneBlockInfo var41 : var37) {
                  var14.setBlock(var41.pos, Blocks.BARRIER.defaultBlockState(), var31 | 816);
               }

               int var40 = 0;

               for(CloneBlockInfo var45 : var34) {
                  if (var14.setBlock(var45.pos, var45.state, var31)) {
                     ++var40;
                  }
               }

               for(CloneBlockInfo var46 : var18) {
                  BlockEntity var48 = var14.getBlockEntity(var46.pos);
                  if (var46.blockEntityInfo != null && var48 != null) {
                     var48.loadCustomOnly(var46.blockEntityInfo.tag, var14.registryAccess());
                     var48.setComponents(var46.blockEntityInfo.components);
                     var48.setChanged();
                  }

                  var14.setBlock(var46.pos, var46.state, var31);
               }

               if (!var6) {
                  for(CloneBlockInfo var47 : var37) {
                     var14.updateNeighboursOnBlockSet(var47.pos, var47.previousStateAtDestination);
                  }
               }

               var14.getBlockTicks().copyAreaFrom(var13.getBlockTicks(), var9, var21);
               if (var40 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  var0.sendSuccess(() -> Component.translatable("commands.clone.success", var40), true);
                  return var40;
               }
            }
         } else {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
         }
      }
   }

   static record DimensionAndPosition(ServerLevel dimension, BlockPos position) {
      DimensionAndPosition(ServerLevel var1, BlockPos var2) {
         super();
         this.dimension = var1;
         this.position = var2;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(final boolean var3) {
         this.canOverlap = var3;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{FORCE, MOVE, NORMAL};
      }
   }

   static record CloneBlockEntityInfo(CompoundTag tag, DataComponentMap components) {
      final CompoundTag tag;
      final DataComponentMap components;

      CloneBlockEntityInfo(CompoundTag var1, DataComponentMap var2) {
         super();
         this.tag = var1;
         this.components = var2;
      }
   }

   static record CloneBlockInfo(BlockPos pos, BlockState state, @Nullable CloneBlockEntityInfo blockEntityInfo, BlockState previousStateAtDestination) {
      final BlockPos pos;
      final BlockState state;
      @Nullable
      final CloneBlockEntityInfo blockEntityInfo;
      final BlockState previousStateAtDestination;

      CloneBlockInfo(BlockPos var1, BlockState var2, @Nullable CloneBlockEntityInfo var3, BlockState var4) {
         super();
         this.pos = var1;
         this.state = var2;
         this.blockEntityInfo = var3;
         this.previousStateAtDestination = var4;
      }
   }
}
