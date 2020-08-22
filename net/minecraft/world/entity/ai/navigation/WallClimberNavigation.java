package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class WallClimberNavigation extends GroundPathNavigation {
   private BlockPos pathToPosition;

   public WallClimberNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   public Path createPath(BlockPos var1, int var2) {
      this.pathToPosition = var1;
      return super.createPath(var1, var2);
   }

   public Path createPath(Entity var1, int var2) {
      this.pathToPosition = new BlockPos(var1);
      return super.createPath(var1, var2);
   }

   public boolean moveTo(Entity var1, double var2) {
      Path var4 = this.createPath((Entity)var1, 0);
      if (var4 != null) {
         return this.moveTo(var4, var2);
      } else {
         this.pathToPosition = new BlockPos(var1);
         this.speedModifier = var2;
         return true;
      }
   }

   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else {
         if (this.pathToPosition != null) {
            if (!this.pathToPosition.closerThan(this.mob.position(), (double)this.mob.getBbWidth()) && (this.mob.getY() <= (double)this.pathToPosition.getY() || !(new BlockPos((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ())).closerThan(this.mob.position(), (double)this.mob.getBbWidth()))) {
               this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            } else {
               this.pathToPosition = null;
            }
         }

      }
   }
}
