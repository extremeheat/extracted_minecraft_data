package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class PistonStructureResolver {
   public static final int MAX_PUSH_DEPTH = 12;
   private final Level level;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos startPos;
   private final Direction pushDirection;
   private final List<BlockPos> toPush = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();
   private final Direction pistonDirection;

   public PistonStructureResolver(Level var1, BlockPos var2, Direction var3, boolean var4) {
      super();
      this.level = var1;
      this.pistonPos = var2;
      this.pistonDirection = var3;
      this.extending = var4;
      if (var4) {
         this.pushDirection = var3;
         this.startPos = var2.relative(var3);
      } else {
         this.pushDirection = var3.getOpposite();
         this.startPos = var2.relative((Direction)var3, 2);
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
            if (isSticky(this.level.getBlockState(var3)) && !this.addBranchingBlocks(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isSticky(BlockState var0) {
      return var0.is(Blocks.SLIME_BLOCK) || var0.is(Blocks.HONEY_BLOCK);
   }

   private static boolean canStickToEachOther(BlockState var0, BlockState var1) {
      if (var0.is(Blocks.HONEY_BLOCK) && var1.is(Blocks.SLIME_BLOCK)) {
         return false;
      } else if (var0.is(Blocks.SLIME_BLOCK) && var1.is(Blocks.HONEY_BLOCK)) {
         return false;
      } else {
         return isSticky(var0) || isSticky(var1);
      }
   }

   private boolean addBlockLine(BlockPos var1, Direction var2) {
      BlockState var3 = this.level.getBlockState(var1);
      if (var3.isAir()) {
         return true;
      } else if (!PistonBaseBlock.isPushable(var3, this.level, var1, this.pushDirection, false, var2)) {
         return true;
      } else if (var1.equals(this.pistonPos)) {
         return true;
      } else if (this.toPush.contains(var1)) {
         return true;
      } else {
         int var4 = 1;
         if (var4 + this.toPush.size() > 12) {
            return false;
         } else {
            while(isSticky(var3)) {
               BlockPos var5 = var1.relative(this.pushDirection.getOpposite(), var4);
               BlockState var6 = var3;
               var3 = this.level.getBlockState(var5);
               if (var3.isAir() || !canStickToEachOther(var6, var3) || !PistonBaseBlock.isPushable(var3, this.level, var5, this.pushDirection, false, this.pushDirection.getOpposite()) || var5.equals(this.pistonPos)) {
                  break;
               }

               ++var4;
               if (var4 + this.toPush.size() > 12) {
                  return false;
               }
            }

            int var11 = 0;

            int var12;
            for(var12 = var4 - 1; var12 >= 0; --var12) {
               this.toPush.add(var1.relative(this.pushDirection.getOpposite(), var12));
               ++var11;
            }

            var12 = 1;

            while(true) {
               BlockPos var7 = var1.relative(this.pushDirection, var12);
               int var8 = this.toPush.indexOf(var7);
               if (var8 > -1) {
                  this.reorderListAtCollision(var11, var8);

                  for(int var9 = 0; var9 <= var8 + var11; ++var9) {
                     BlockPos var10 = (BlockPos)this.toPush.get(var9);
                     if (isSticky(this.level.getBlockState(var10)) && !this.addBranchingBlocks(var10)) {
                        return false;
                     }
                  }

                  return true;
               }

               var3 = this.level.getBlockState(var7);
               if (var3.isAir()) {
                  return true;
               }

               if (!PistonBaseBlock.isPushable(var3, this.level, var7, this.pushDirection, true, this.pushDirection) || var7.equals(this.pistonPos)) {
                  return false;
               }

               if (var3.getPistonPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(var7);
                  return true;
               }

               if (this.toPush.size() >= 12) {
                  return false;
               }

               this.toPush.add(var7);
               ++var11;
               ++var12;
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
            if (canStickToEachOther(var8, var2) && !this.addBlockLine(var7, var6)) {
               return false;
            }
         }
      }

      return true;
   }

   public Direction getPushDirection() {
      return this.pushDirection;
   }

   public List<BlockPos> getToPush() {
      return this.toPush;
   }

   public List<BlockPos> getToDestroy() {
      return this.toDestroy;
   }
}
