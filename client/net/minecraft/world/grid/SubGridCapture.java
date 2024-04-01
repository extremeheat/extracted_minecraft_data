package net.minecraft.world.grid;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FloataterBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public record SubGridCapture(SubGridBlocks a, LongSet b, BlockPos c, int d) {
   private final SubGridBlocks blocks;
   private final LongSet mask;
   private final BlockPos minPos;
   private final int engines;
   private static final Direction[] DIRECTIONS = Direction.values();

   public SubGridCapture(SubGridBlocks var1, LongSet var2, BlockPos var3, int var4) {
      super();
      this.blocks = var1;
      this.mask = var2;
      this.minPos = var3;
      this.engines = var4;
   }

   @Nullable
   public static SubGridCapture scan(Level var0, BlockPos var1, Direction var2) {
      Long2ObjectOpenHashMap var3 = new Long2ObjectOpenHashMap();
      LongArrayFIFOQueue var4 = new LongArrayFIFOQueue();
      var3.put(var1.asLong(), var0.getBlockState(var1));
      var4.enqueue(var1.asLong());
      int var5 = var1.getX();
      int var6 = var1.getY();
      int var7 = var1.getZ();
      int var8 = var1.getX();
      int var9 = var1.getY();
      int var10 = var1.getZ();
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();
      int var13 = var0.getGameRules().getInt(GameRules.RULE_FLOATATER_SIZE_LIMIT);

      while(!var4.isEmpty()) {
         long var14 = var4.dequeueLastLong();
         var11.set(var14);
         BlockState var16 = (BlockState)var3.get(var14);
         var5 = Math.min(var5, var11.getX());
         var6 = Math.min(var6, var11.getY());
         var7 = Math.min(var7, var11.getZ());
         var8 = Math.max(var8, var11.getX());
         var9 = Math.max(var9, var11.getY());
         var10 = Math.max(var10, var11.getZ());
         if (var8 - var5 + 1 > var13 || var9 - var6 + 1 > var13 || var10 - var7 + 1 > var13) {
            return null;
         }

         VoxelShape var17 = var16.getShape(var0, var11);

         for(Direction var21 : DIRECTIONS) {
            var12.setWithOffset(var11, var21);
            long var22 = var12.asLong();
            if (!var3.containsKey(var22)) {
               BlockState var24 = var0.getBlockState(var12);
               VoxelShape var25 = var24.getShape(var0, var12);
               boolean var26 = var21 == var2 && !var24.canBeReplaced();
               if (var26 || isConnected(var21, var17, var25, var16, var24)) {
                  var4.enqueue(var22);
                  var3.put(var22, var24);
               }
            }
         }
      }

      int var27 = var8 - var5 + 1;
      int var15 = var9 - var6 + 1;
      int var28 = var10 - var7 + 1;
      SubGridBlocks var29 = new SubGridBlocks(var27, var15, var28);
      int var30 = 0;
      ObjectIterator var31 = Long2ObjectMaps.fastIterable(var3).iterator();

      while(var31.hasNext()) {
         Entry var32 = (Entry)var31.next();
         var11.set(var32.getLongKey());
         BlockState var33 = ((BlockState)var32.getValue()).trySetValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
         if (var33.is(Blocks.FLOATATER) && var33.getValue(FloataterBlock.FACING) == var2 && var33.getValue(FloataterBlock.TRIGGERED)) {
            ++var30;
         }

         if (var33.getBlock() instanceof FlyingTickable) {
            var29.markTickable(new BlockPos(var11.getX() - var5, var11.getY() - var6, var11.getZ() - var7));
         }

         if (!var33.hasBlockEntity()) {
            var29.setBlockState(var11.getX() - var5, var11.getY() - var6, var11.getZ() - var7, var33);
         }
      }

      return new SubGridCapture(var29, new LongOpenHashSet(var3.keySet()), new BlockPos(var5, var6, var7), var30);
   }

   private static boolean isConnected(Direction var0, VoxelShape var1, VoxelShape var2, BlockState var3, BlockState var4) {
      if (isStickyInDirection(var3, var0) || isStickyInDirection(var4, var0.getOpposite())) {
         return true;
      } else if (!areShapesConnected(var0, var1, var2)) {
         return false;
      } else {
         return !isNonStickyInDirection(var3, var0) && !isNonStickyInDirection(var4, var0.getOpposite());
      }
   }

   private static boolean areShapesConnected(Direction var0, VoxelShape var1, VoxelShape var2) {
      if (var1 != Shapes.empty() && var2 != Shapes.empty()) {
         VoxelShape var3 = Shapes.getFaceShape(var1, var0);
         VoxelShape var4 = Shapes.getFaceShape(var2, var0.getOpposite());
         return var3 == Shapes.block() && var4 == Shapes.block() ? true : Shapes.joinIsNotEmpty(var3, var4, BooleanOp.AND);
      } else {
         return false;
      }
   }

   private static boolean isNonStickyInDirection(BlockState var0, Direction var1) {
      return var0.is(Blocks.FLOATATER) && var0.getValue(FloataterBlock.FACING) != var1;
   }

   private static boolean isStickyInDirection(BlockState var0, Direction var1) {
      if (!var0.is(Blocks.SLIME_BLOCK) && !var0.is(Blocks.HONEY_BLOCK)) {
         if (var0.is(Blocks.STICKY_PISTON) && var0.getValue(PistonBaseBlock.FACING) == var1) {
            return true;
         } else {
            return var0.is(Blocks.FLOATATER) && var0.getValue(FloataterBlock.FACING) == var1;
         }
      } else {
         return true;
      }
   }

   public void remove(Level var1) {
      this.forEachPos(var1x -> {
         BlockState var2 = var1.getBlockState(var1x);
         if (var2.hasBlockEntity()) {
            var1.destroyBlock(var1x, true);
         } else {
            FluidState var3 = var2.getFluidState();
            BlockState var4 = var3.createLegacyBlock();
            var1.setBlock(var1x, var4, 18);
         }
      });
      this.forEachPos(var1x -> var1.blockUpdated(var1x, var1.getBlockState(var1x).getBlock()));
   }

   private void forEachPos(Consumer<BlockPos> var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      LongIterator var3 = this.mask.longIterator();

      while(var3.hasNext()) {
         var2.set(var3.nextLong());
         var1.accept(var2);
      }
   }
}
