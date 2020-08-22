package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class PistonStructureResolver {
   private final Level level;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos startPos;
   private final Direction pushDirection;
   private final List toPush = Lists.newArrayList();
   private final List toDestroy = Lists.newArrayList();
   private final Direction pistonDirection;

   public PistonStructureResolver(Level var1, BlockPos var2, Direction var3, boolean var4) {
      this.level = var1;
      this.pistonPos = var2;
      this.pistonDirection = var3;
      this.extending = var4;
      if (var4) {
         this.pushDirection = var3;
         this.startPos = var2.relative(var3);
      } else {
         this.pushDirection = var3.getOpposite();
         this.startPos = var2.relative(var3, 2);
      }

   }

   public boolean resolve() {
      this.toPush.clear();
      this.toDestroy.clear();
      BlockState var1 = this.level.getBlockState(this.startPos);
      if (!PistonBaseBlock.isPushable(var1, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
         if (this.extending && var1.getPistonPushReaction() == PushReaction.DESTROY) {
            this.toDestroy.add(this.startPos);
            return true;
         } else {
            return false;
         }
      } else if (!this.addBlockLine(this.startPos, this.pushDirection)) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.toPush.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.toPush.get(var2);
            if (isSticky(this.level.getBlockState(var3).getBlock()) && !this.addBranchingBlocks(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isSticky(Block var0) {
      return var0 == Blocks.SLIME_BLOCK || var0 == Blocks.HONEY_BLOCK;
   }

   private static boolean canStickToEachOther(Block var0, Block var1) {
      if (var0 == Blocks.HONEY_BLOCK && var1 == Blocks.SLIME_BLOCK) {
         return false;
      } else if (var0 == Blocks.SLIME_BLOCK && var1 == Blocks.HONEY_BLOCK) {
         return false;
      } else {
         return isSticky(var0) || isSticky(var1);
      }
   }

   private boolean addBlockLine(BlockPos var1, Direction var2) {
      BlockState var3 = this.level.getBlockState(var1);
      Block var4 = var3.getBlock();
      if (var3.isAir()) {
         return true;
      } else if (!PistonBaseBlock.isPushable(var3, this.level, var1, this.pushDirection, false, var2)) {
         return true;
      } else if (var1.equals(this.pistonPos)) {
         return true;
      } else if (this.toPush.contains(var1)) {
         return true;
      } else {
         int var5 = 1;
         if (var5 + this.toPush.size() > 12) {
            return false;
         } else {
            while(isSticky(var4)) {
               BlockPos var6 = var1.relative(this.pushDirection.getOpposite(), var5);
               Block var7 = var4;
               var3 = this.level.getBlockState(var6);
               var4 = var3.getBlock();
               if (var3.isAir() || !canStickToEachOther(var7, var4) || !PistonBaseBlock.isPushable(var3, this.level, var6, this.pushDirection, false, this.pushDirection.getOpposite()) || var6.equals(this.pistonPos)) {
                  break;
               }

               ++var5;
               if (var5 + this.toPush.size() > 12) {
                  return false;
               }
            }

            int var12 = 0;

            int var13;
            for(var13 = var5 - 1; var13 >= 0; --var13) {
               this.toPush.add(var1.relative(this.pushDirection.getOpposite(), var13));
               ++var12;
            }

            var13 = 1;

            while(true) {
               BlockPos var8 = var1.relative(this.pushDirection, var13);
               int var9 = this.toPush.indexOf(var8);
               if (var9 > -1) {
                  this.reorderListAtCollision(var12, var9);

                  for(int var10 = 0; var10 <= var9 + var12; ++var10) {
                     BlockPos var11 = (BlockPos)this.toPush.get(var10);
                     if (isSticky(this.level.getBlockState(var11).getBlock()) && !this.addBranchingBlocks(var11)) {
                        return false;
                     }
                  }

                  return true;
               }

               var3 = this.level.getBlockState(var8);
               if (var3.isAir()) {
                  return true;
               }

               if (!PistonBaseBlock.isPushable(var3, this.level, var8, this.pushDirection, true, this.pushDirection) || var8.equals(this.pistonPos)) {
                  return false;
               }

               if (var3.getPistonPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(var8);
                  return true;
               }

               if (this.toPush.size() >= 12) {
                  return false;
               }

               this.toPush.add(var8);
               ++var12;
               ++var13;
            }
         }
      }
   }

   private void reorderListAtCollision(int var1, int var2) {
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      var3.addAll(this.toPush.subList(0, var2));
      var4.addAll(this.toPush.subList(this.toPush.size() - var1, this.toPush.size()));
      var5.addAll(this.toPush.subList(var2, this.toPush.size() - var1));
      this.toPush.clear();
      this.toPush.addAll(var3);
      this.toPush.addAll(var4);
      this.toPush.addAll(var5);
   }

   private boolean addBranchingBlocks(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction var6 = var3[var5];
         if (var6.getAxis() != this.pushDirection.getAxis()) {
            BlockPos var7 = var1.relative(var6);
            BlockState var8 = this.level.getBlockState(var7);
            if (canStickToEachOther(var8.getBlock(), var2.getBlock()) && !this.addBlockLine(var7, var6)) {
               return false;
            }
         }
      }

      return true;
   }

   public List getToPush() {
      return this.toPush;
   }

   public List getToDestroy() {
      return this.toDestroy;
   }
}
