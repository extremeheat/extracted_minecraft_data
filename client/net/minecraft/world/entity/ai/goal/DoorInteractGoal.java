package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class DoorInteractGoal extends Goal {
   protected Mob mob;
   protected BlockPos doorPos;
   protected boolean hasDoor;
   private boolean passed;
   private float doorOpenDirX;
   private float doorOpenDirZ;

   public DoorInteractGoal(final Mob mob) {
      super();
      this.doorPos = BlockPos.ZERO;
      this.mob = mob;
      if (!GoalUtils.hasGroundPathNavigation(mob)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean isOpen() {
      if (!this.hasDoor) {
         return false;
      } else {
         BlockState blockState = this.mob.level().getBlockState(this.doorPos);
         if (!(blockState.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
         } else {
            return (Boolean)blockState.getValue(DoorBlock.OPEN);
         }
      }
   }

   protected void setOpen(final boolean open) {
      if (this.hasDoor) {
         BlockState blockState = this.mob.level().getBlockState(this.doorPos);
         if (blockState.getBlock() instanceof DoorBlock) {
            ((DoorBlock)blockState.getBlock()).setOpen(this.mob, this.mob.level(), blockState, this.doorPos, open);
         }
      }

   }

   public boolean canUse() {
      if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
         return false;
      } else if (!this.mob.horizontalCollision) {
         return false;
      } else {
         Path path = this.mob.getNavigation().getPath();
         if (path != null && !path.isDone()) {
            for(int i = 0; i < Math.min(path.getNextNodeIndex() + 2, path.getNodeCount()); ++i) {
               Node node = path.getNode(i);
               this.doorPos = new BlockPos(node.x, node.y + 1, node.z);
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

   public boolean canContinueToUse() {
      return !this.passed;
   }

   public void start() {
      this.passed = false;
      this.doorOpenDirX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
      this.doorOpenDirZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      float newDoorDirX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
      float newDoorDirZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
      float dot = this.doorOpenDirX * newDoorDirX + this.doorOpenDirZ * newDoorDirZ;
      if (dot < 0.0F) {
         this.passed = true;
      }

   }
}
