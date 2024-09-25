package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class DoorInteractGoal extends Goal {
   protected Mob mob;
   protected BlockPos doorPos = BlockPos.ZERO;
   protected boolean hasDoor;
   private boolean passed;
   private float doorOpenDirX;
   private float doorOpenDirZ;

   public DoorInteractGoal(Mob var1) {
      super();
      this.mob = var1;
      if (!GoalUtils.hasGroundPathNavigation(var1)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean isOpen() {
      if (!this.hasDoor) {
         return false;
      } else {
         BlockState var1 = this.mob.level().getBlockState(this.doorPos);
         if (!(var1.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
         } else {
            return var1.getValue(DoorBlock.OPEN);
         }
      }
   }

   protected void setOpen(boolean var1) {
      if (this.hasDoor) {
         BlockState var2 = this.mob.level().getBlockState(this.doorPos);
         if (var2.getBlock() instanceof DoorBlock) {
            ((DoorBlock)var2.getBlock()).setOpen(this.mob, this.mob.level(), var2, this.doorPos, var1);
         }
      }
   }

   @Override
   public boolean canUse() {
      if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
         return false;
      } else if (!this.mob.horizontalCollision) {
         return false;
      } else {
         GroundPathNavigation var1 = (GroundPathNavigation)this.mob.getNavigation();
         Path var2 = var1.getPath();
         if (var2 != null && !var2.isDone() && var1.canOpenDoors()) {
            for (int var3 = 0; var3 < Math.min(var2.getNextNodeIndex() + 2, var2.getNodeCount()); var3++) {
               Node var4 = var2.getNode(var3);
               this.doorPos = new BlockPos(var4.x, var4.y + 1, var4.z);
               if (!(this.mob.distanceToSqr((double)this.doorPos.getX(), this.mob.getY(), (double)this.doorPos.getZ()) > 2.25)) {
                  this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level(), this.doorPos);
                  if (this.hasDoor) {
                     return true;
                  }
               }
            }

            this.doorPos = this.mob.blockPosition().above();
            this.hasDoor = DoorBlock.isWoodenDoor(this.mob.level(), this.doorPos);
            return this.hasDoor;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean canContinueToUse() {
      return !this.passed;
   }

   @Override
   public void start() {
      this.passed = false;
      this.doorOpenDirX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
      this.doorOpenDirZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }

   @Override
   public void tick() {
      float var1 = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
      float var2 = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
      float var3 = this.doorOpenDirX * var1 + this.doorOpenDirZ * var2;
      if (var3 < 0.0F) {
         this.passed = true;
      }
   }
}