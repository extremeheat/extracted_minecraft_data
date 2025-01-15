package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.fill.toobig", var0, var1));
   static final BlockInput HOLLOW_CORE;
   private static final SimpleCommandExceptionType ERROR_FAILED;

   public FillCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(wrapWithMode(var1, Commands.argument("block", BlockStateArgument.block(var1)), (var0x) -> BlockPosArgument.getLoadedBlockPos(var0x, "from"), (var0x) -> BlockPosArgument.getLoadedBlockPos(var0x, "to"), (var0x) -> BlockStateArgument.getBlock(var0x, "block"), (var0x) -> null).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((var0x) -> fillBlocks((CommandSourceStack)var0x.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, (Predicate)null, false))).then(wrapWithMode(var1, Commands.argument("filter", BlockPredicateArgument.blockPredicate(var1)), (var0x) -> BlockPosArgument.getLoadedBlockPos(var0x, "from"), (var0x) -> BlockPosArgument.getLoadedBlockPos(var0x, "to"), (var0x) -> BlockStateArgument.getBlock(var0x, "block"), (var0x) -> BlockPredicateArgument.getBlockPredicate(var0x, "filter")))).then(Commands.literal("keep").executes((var0x) -> fillBlocks((CommandSourceStack)var0x.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(var0x, "from"), BlockPosArgument.getLoadedBlockPos(var0x, "to")), BlockStateArgument.getBlock(var0x, "block"), FillCommand.Mode.REPLACE, (var0) -> var0.getLevel().isEmptyBlock(var0.getPos()), false)))))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapWithMode(CommandBuildContext var0, ArgumentBuilder<CommandSourceStack, ?> var1, InCommandFunction<CommandContext<CommandSourceStack>, BlockPos> var2, InCommandFunction<CommandContext<CommandSourceStack>, BlockPos> var3, InCommandFunction<CommandContext<CommandSourceStack>, BlockInput> var4, NullableCommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> var5) {
      return var1.executes((var4x) -> fillBlocks((CommandSourceStack)var4x.getSource(), BoundingBox.fromCorners((Vec3i)var2.apply(var4x), (Vec3i)var3.apply(var4x)), (BlockInput)var4.apply(var4x), FillCommand.Mode.REPLACE, (Predicate)var5.apply(var4x), false)).then(Commands.literal("outline").executes((var4x) -> fillBlocks((CommandSourceStack)var4x.getSource(), BoundingBox.fromCorners((Vec3i)var2.apply(var4x), (Vec3i)var3.apply(var4x)), (BlockInput)var4.apply(var4x), FillCommand.Mode.OUTLINE, (Predicate)var5.apply(var4x), false))).then(Commands.literal("hollow").executes((var4x) -> fillBlocks((CommandSourceStack)var4x.getSource(), BoundingBox.fromCorners((Vec3i)var2.apply(var4x), (Vec3i)var3.apply(var4x)), (BlockInput)var4.apply(var4x), FillCommand.Mode.HOLLOW, (Predicate)var5.apply(var4x), false))).then(Commands.literal("destroy").executes((var4x) -> fillBlocks((CommandSourceStack)var4x.getSource(), BoundingBox.fromCorners((Vec3i)var2.apply(var4x), (Vec3i)var3.apply(var4x)), (BlockInput)var4.apply(var4x), FillCommand.Mode.DESTROY, (Predicate)var5.apply(var4x), false))).then(Commands.literal("strict").executes((var4x) -> fillBlocks((CommandSourceStack)var4x.getSource(), BoundingBox.fromCorners((Vec3i)var2.apply(var4x), (Vec3i)var3.apply(var4x)), (BlockInput)var4.apply(var4x), FillCommand.Mode.REPLACE, (Predicate)var5.apply(var4x), true)));
   }

   private static int fillBlocks(CommandSourceStack var0, BoundingBox var1, BlockInput var2, Mode var3, @Nullable Predicate<BlockInWorld> var4, boolean var5) throws CommandSyntaxException {
      int var6 = var1.getXSpan() * var1.getYSpan() * var1.getZSpan();
      int var7 = var0.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
      if (var6 > var7) {
         throw ERROR_AREA_TOO_LARGE.create(var7, var6);
      } else {
         ArrayList var8 = Lists.newArrayList();
         ServerLevel var9 = var0.getLevel();
         if (var9.isDebug()) {
            throw ERROR_FAILED.create();
         } else {
            int var10 = 0;

            for(BlockPos var12 : BlockPos.betweenClosed(var1.minX(), var1.minY(), var1.minZ(), var1.maxX(), var1.maxY(), var1.maxZ())) {
               if (var4 == null || var4.test(new BlockInWorld(var9, var12, true))) {
                  boolean var13 = false;
                  if (var3.affector.affect(var9, var12)) {
                     var13 = true;
                  }

                  BlockInput var14 = var3.filter.filter(var1, var12, var2, var9);
                  if (var14 == null) {
                     if (var13) {
                        ++var10;
                     }
                  } else if (!var14.place(var9, var12, 2 | (var5 ? 304 : 256))) {
                     if (var13) {
                        ++var10;
                     }
                  } else {
                     if (!var5) {
                        var8.add(var12.immutable());
                     }

                     ++var10;
                  }
               }
            }

            for(BlockPos var16 : var8) {
               Block var17 = var9.getBlockState(var16).getBlock();
               var9.updateNeighborsAt(var16, var17);
            }

            if (var10 == 0) {
               throw ERROR_FAILED.create();
            } else {
               var0.sendSuccess(() -> Component.translatable("commands.fill.success", var10), true);
               return var10;
            }
         }
      }
   }

   static {
      HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
      ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));
   }

   static enum Mode {
      REPLACE(FillCommand.Affector.NOOP, FillCommand.Filter.NOOP),
      OUTLINE(FillCommand.Affector.NOOP, (var0, var1, var2, var3) -> var1.getX() != var0.minX() && var1.getX() != var0.maxX() && var1.getY() != var0.minY() && var1.getY() != var0.maxY() && var1.getZ() != var0.minZ() && var1.getZ() != var0.maxZ() ? null : var2),
      HOLLOW(FillCommand.Affector.NOOP, (var0, var1, var2, var3) -> var1.getX() != var0.minX() && var1.getX() != var0.maxX() && var1.getY() != var0.minY() && var1.getY() != var0.maxY() && var1.getZ() != var0.minZ() && var1.getZ() != var0.maxZ() ? FillCommand.HOLLOW_CORE : var2),
      DESTROY((var0, var1) -> var0.destroyBlock(var1, true), FillCommand.Filter.NOOP);

      public final Filter filter;
      public final Affector affector;

      private Mode(final Affector var3, final Filter var4) {
         this.affector = var3;
         this.filter = var4;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
      }
   }

   @FunctionalInterface
   public interface Filter {
      Filter NOOP = (var0, var1, var2, var3) -> var2;

      @Nullable
      BlockInput filter(BoundingBox var1, BlockPos var2, BlockInput var3, ServerLevel var4);
   }

   @FunctionalInterface
   public interface Affector {
      Affector NOOP = (var0, var1) -> false;

      boolean affect(ServerLevel var1, BlockPos var2);
   }

   @FunctionalInterface
   interface NullableCommandFunction<T, R> {
      @Nullable
      R apply(T var1) throws CommandSyntaxException;
   }
}
