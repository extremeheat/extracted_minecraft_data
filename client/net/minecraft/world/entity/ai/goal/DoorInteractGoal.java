package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public abstract class DoorInteractGoal extends Goal {
   protected Mob mob;
   protected BlockPos doorPos;
   protected boolean hasDoor;
   private boolean passed;
   private float doorOpenDirX;
   private float doorOpenDirZ;

   public DoorInteractGoal(Mob var1) {
      super();
      this.doorPos = BlockPos.ZERO;
      this.mob = var1;
      if (!(var1.getNavigation() instanceof GroundPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean isOpen() {
      if (!this.hasDoor) {
         return false;
      } else {
         BlockState var1 = this.mob.level.getBlockState(this.doorPos);
         if (!(var1.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
         } else {
            return (Boolean)var1.getValue(DoorBlock.OPEN);
         }
      }
   }

   protected void setOpen(boolean var1) {
      if (this.hasDoor) {
         BlockState var2 = this.mob.level.getBlockState(this.doorPos);
         if (var2.getBlock() instanceof DoorBlock) {
            ((DoorBlock)var2.getBlock()).setOpen(this.mob.level, this.doorPos, var1);
         }
      }

   }

   public boolean canUse() {
      if (!this.mob.horizontalCollision) {
         return false;
      } else {
         GroundPathNavigation var1 = (GroundPathNavigation)this.mob.getNavigation();
         Path var2 = var1.getPath();
         if (var2 != null && !var2.isDone() && var1.canOpenDoors()) {
            for(int var3 = 0; var3 < Math.min(var2.getIndex() + 2, var2.getSize()); ++var3) {
               Node var4 = var2.get(var3);
               this.doorPos = new BlockPos(var4.x, var4.y + 1, var4.z);
               if (this.mob.distanceToSqr((double)this.doorPos.getX(), this.mob.y, (double)this.doorPos.getZ()) <= 2.25D) {
                  this.hasDoor = isDoor(this.mob.level, this.doorPos);
                  if (this.hasDoor) {
                     return true;
                  }
               }
            }

            this.doorPos = (new BlockPos(this.mob)).above();
            this.hasDoor = isDoor(this.mob.level, this.doorPos);
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
      this.doorOpenDirX = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.mob.x);
      this.doorOpenDirZ = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.mob.z);
   }

   public void tick() {
      float var1 = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.mob.x);
      float var2 = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.mob.z);
      float var3 = this.doorOpenDirX * var1 + this.doorOpenDirZ * var2;
      if (var3 < 0.0F) {
         this.passed = true;
      }

   }

   public static boolean isDoor(Level var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.getBlock() instanceof DoorBlock && var2.getMaterial() == Material.WOOD;
   }
}
