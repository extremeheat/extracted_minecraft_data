package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.clone.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.failed"));
   public static final Predicate<BlockInWorld> FILTER_AIR = (var0) -> {
      return !var0.getState().isAir();
   };

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), (var0) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), FILTER_AIR, CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), FILTER_AIR, CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      })))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), BlockPredicateArgument.getBlockPredicate(var0x, "filter"), CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), BlockPredicateArgument.getBlockPredicate(var0x, "filter"), CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), BlockPredicateArgument.getBlockPredicate(var0x, "filter"), CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((var0x) -> {
         return clone((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "begin"), BlockPosArgument.getLoadedBlockPos(var0x, "end"), BlockPosArgument.getLoadedBlockPos(var0x, "destination"), BlockPredicateArgument.getBlockPredicate(var0x, "filter"), CloneCommands.Mode.NORMAL);
      }))))))));
   }

   private static int clone(CommandSourceStack var0, BlockPos var1, BlockPos var2, BlockPos var3, Predicate<BlockInWorld> var4, CloneCommands.Mode var5) throws CommandSyntaxException {
      BoundingBox var6 = new BoundingBox(var1, var2);
      BlockPos var7 = var3.offset(var6.getLength());
      BoundingBox var8 = new BoundingBox(var3, var7);
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
               BlockPos var15 = new BlockPos(var8.x0 - var6.x0, var8.y0 - var6.y0, var8.z0 - var6.z0);

               int var18;
               for(int var16 = var6.z0; var16 <= var6.z1; ++var16) {
                  for(int var17 = var6.y0; var17 <= var6.y1; ++var17) {
                     for(var18 = var6.x0; var18 <= var6.x1; ++var18) {
                        BlockPos var19 = new BlockPos(var18, var17, var16);
                        BlockPos var20 = var19.offset(var15);
                        BlockInWorld var21 = new BlockInWorld(var10, var19, false);
                        BlockState var22 = var21.getState();
                        if (var4.test(var21)) {
                           BlockEntity var23 = var10.getBlockEntity(var19);
                           if (var23 != null) {
                              CompoundTag var24 = var23.save(new CompoundTag());
                              var12.add(new CloneCommands.CloneBlockInfo(var20, var22, var24));
                              var14.addLast(var19);
                           } else if (!var22.isSolidRender(var10, var19) && !var22.isCollisionShapeFullBlock(var10, var19)) {
                              var13.add(new CloneCommands.CloneBlockInfo(var20, var22, (CompoundTag)null));
                              var14.addFirst(var19);
                           } else {
                              var11.add(new CloneCommands.CloneBlockInfo(var20, var22, (CompoundTag)null));
                              var14.addLast(var19);
                           }
                        }
                     }
                  }
               }

               if (var5 == CloneCommands.Mode.MOVE) {
                  Iterator var25 = var14.iterator();

                  BlockPos var27;
                  while(var25.hasNext()) {
                     var27 = (BlockPos)var25.next();
                     BlockEntity var29 = var10.getBlockEntity(var27);
                     Clearable.tryClear(var29);
                     var10.setBlock(var27, Blocks.BARRIER.defaultBlockState(), 2);
                  }

                  var25 = var14.iterator();

                  while(var25.hasNext()) {
                     var27 = (BlockPos)var25.next();
                     var10.setBlock(var27, Blocks.AIR.defaultBlockState(), 3);
                  }
               }

               ArrayList var26 = Lists.newArrayList();
               var26.addAll(var11);
               var26.addAll(var12);
               var26.addAll(var13);
               List var28 = Lists.reverse(var26);
               Iterator var30 = var28.iterator();

               while(var30.hasNext()) {
                  CloneCommands.CloneBlockInfo var31 = (CloneCommands.CloneBlockInfo)var30.next();
                  BlockEntity var33 = var10.getBlockEntity(var31.pos);
                  Clearable.tryClear(var33);
                  var10.setBlock(var31.pos, Blocks.BARRIER.defaultBlockState(), 2);
               }

               var18 = 0;
               Iterator var32 = var26.iterator();

               CloneCommands.CloneBlockInfo var34;
               while(var32.hasNext()) {
                  var34 = (CloneCommands.CloneBlockInfo)var32.next();
                  if (var10.setBlock(var34.pos, var34.state, 2)) {
                     ++var18;
                  }
               }

               for(var32 = var12.iterator(); var32.hasNext(); var10.setBlock(var34.pos, var34.state, 2)) {
                  var34 = (CloneCommands.CloneBlockInfo)var32.next();
                  BlockEntity var35 = var10.getBlockEntity(var34.pos);
                  if (var34.tag != null && var35 != null) {
                     var34.tag.putInt("x", var34.pos.getX());
                     var34.tag.putInt("y", var34.pos.getY());
                     var34.tag.putInt("z", var34.pos.getZ());
                     var35.load(var34.tag);
                     var35.setChanged();
                  }
               }

               var32 = var28.iterator();

               while(var32.hasNext()) {
                  var34 = (CloneCommands.CloneBlockInfo)var32.next();
                  var10.blockUpdated(var34.pos, var34.state.getBlock());
               }

               var10.getBlockTicks().copy(var6, var15);
               if (var18 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  var0.sendSuccess(new TranslatableComponent("commands.clone.success", new Object[]{var18}), true);
                  return var18;
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
