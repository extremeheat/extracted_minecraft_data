package net.minecraft.world.entity.ai.navigation;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class WallClimberNavigation extends GroundPathNavigation {
   @Nullable
   private BlockPos pathToPosition;

   public WallClimberNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   public Path createPath(BlockPos var1, int var2) {
      this.pathToPosition = var1;
      return super.createPath(var1, var2);
   }

   public Path createPath(Entity var1, int var2) {
      this.pathToPosition = var1.blockPosition();
      return super.createPath(var1, var2);
   }

   public boolean moveTo(Entity var1, double var2) {
      Path var4 = this.createPath((Entity)var1, 0);
      if (var4 != null) {
         return this.moveTo(var4, var2);
      } else {
         this.pathToPosition = var1.blockPosition();
         this.speedModifier = var2;
         return true;
      }
   }

   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else {
         if (this.pathToPosition != null) {
            if (!this.pathToPosition.closerToCenterThan(this.mob.position(), (double)this.mob.getBbWidth()) && (!(this.mob.getY() > (double)this.pathToPosition.getY()) || !BlockPos.containing((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ()).closerToCenterThan(this.mob.position(), (double)this.mob.getBbWidth()))) {
               this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            } else {
               this.pathToPosition = null;
            }
         }

      }
   }
}
