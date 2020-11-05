package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.fill.toobig", new Object[]{var0, var1});
   });
   private static final BlockInput HOLLOW_CORE;
   private static final SimpleCommandExceptionType ERROR_FAILED;

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block()).executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(var0x, "filter"));
      })))).then(Commands.literal("keep").executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, (var0) -> {
            return var0.getLevel().isEmptyBlock(var0.getPos());
         });
      }))).then(Commands.literal("outline").executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.OUTLINE, (Predicate)null);
      }))).then(Commands.literal("hollow").executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.HOLLOW, (Predicate)null);
      }))).then(Commands.literal("destroy").executes((var0x) -> {
         return fillBlocks((CommandSourceStack)var0x.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.DESTROY, (Predicate)null);
      }))))));
   }

   private static int fillBlocks(CommandSourceStack var0, BoundingBox var1, BlockInput var2, FillCommand.Mode var3, @Nullable Predicate<BlockInWorld> var4) throws CommandSyntaxException {
      int var5 = var1.getXSpan() * var1.getYSpan() * var1.getZSpan();
      if (var5 > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, var5);
      } else {
         ArrayList var6 = Lists.newArrayList();
         ServerLevel var7 = var0.getLevel();
         int var8 = 0;
         Iterator var9 = BlockPos.betweenClosed(var1.x0, var1.y0, var1.z0, var1.x1, var1.y1, var1.z1).iterator();

         while(true) {
            BlockPos var10;
            do {
               if (!var9.hasNext()) {
                  var9 = var6.iterator();

                  while(var9.hasNext()) {
                     var10 = (BlockPos)var9.next();
                     Block var13 = var7.getBlockState(var10).getBlock();
                     var7.blockUpdated(var10, var13);
                  }

                  if (var8 == 0) {
                     throw ERROR_FAILED.create();
                  }

                  var0.sendSuccess(new TranslatableComponent("commands.fill.success", new Object[]{var8}), true);
                  return var8;
               }

               var10 = (BlockPos)var9.next();
            } while(var4 != null && !var4.test(new BlockInWorld(var7, var10, true)));

            BlockInput var11 = var3.filter.filter(var1, var10, var2, var7);
            if (var11 != null) {
               BlockEntity var12 = var7.getBlockEntity(var10);
               Clearable.tryClear(var12);
               if (var11.place(var7, var10, 2)) {
                  var6.add(var10.immutable());
                  ++var8;
               }
            }
         }
      }
   }

   static {
      HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
      ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.fill.failed"));
   }

   static enum Mode {
      REPLACE((var0, var1, var2, var3) -> {
         return var2;
      }),
      OUTLINE((var0, var1, var2, var3) -> {
         return var1.getX() != var0.x0 && var1.getX() != var0.x1 && var1.getY() != var0.y0 && var1.getY() != var0.y1 && var1.getZ() != var0.z0 && var1.getZ() != var0.z1 ? null : var2;
      }),
      HOLLOW((var0, var1, var2, var3) -> {
         return var1.getX() != var0.x0 && var1.getX() != var0.x1 && var1.getY() != var0.y0 && var1.getY() != var0.y1 && var1.getZ() != var0.z0 && var1.getZ() != var0.z1 ? FillCommand.HOLLOW_CORE : var2;
      }),
      DESTROY((var0, var1, var2, var3) -> {
         var3.destroyBlock(var1, true);
         return var2;
      });

      public final SetBlockCommand.Filter filter;

      private Mode(SetBlockCommand.Filter var3) {
         this.filter = var3;
      }
   }
}
