package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class FollowOwnerGoal extends Goal {
   protected final TamableAnimal tamable;
   private LivingEntity owner;
   protected final LevelReader level;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private final float startDistance;
   private float oldWaterCost;

   public FollowOwnerGoal(TamableAnimal var1, double var2, float var4, float var5) {
      super();
      this.tamable = var1;
      this.level = var1.level;
      this.speedModifier = var2;
      this.navigation = var1.getNavigation();
      this.startDistance = var4;
      this.stopDistance = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(var1.getNavigation() instanceof GroundPathNavigation) && !(var1.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean canUse() {
      LivingEntity var1 = this.tamable.getOwner();
      if (var1 == null) {
         return false;
      } else if (var1 instanceof Player && ((Player)var1).isSpectator()) {
         return false;
      } else if (this.tamable.isSitting()) {
         return false;
      } else if (this.tamable.distanceToSqr(var1) < (double)(this.startDistance * this.startDistance)) {
         return false;
      } else {
         this.owner = var1;
         return true;
      }
   }

   public boolean canContinueToUse() {
      return !this.navigation.isDone() && this.tamable.distanceToSqr(this.owner) > (double)(this.stopDistance * this.stopDistance) && !this.tamable.isSitting();
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
      this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   public void stop() {
      this.owner = null;
      this.navigation.stop();
      this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
   }

   public void tick() {
      this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
      if (!this.tamable.isSitting()) {
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.navigation.moveTo((Entity)this.owner, this.speedModifier)) {
               if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                  if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                     int var1 = Mth.floor(this.owner.x) - 2;
                     int var2 = Mth.floor(this.owner.z) - 2;
                     int var3 = Mth.floor(this.owner.getBoundingBox().minY);

                     for(int var4 = 0; var4 <= 4; ++var4) {
                        for(int var5 = 0; var5 <= 4; ++var5) {
                           if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && this.isTeleportFriendlyBlock(new BlockPos(var1 + var4, var3 - 1, var2 + var5))) {
                              this.tamable.moveTo((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.tamable.yRot, this.tamable.xRot);
                              this.navigation.stop();
                              return;
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   protected boolean isTeleportFriendlyBlock(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      return var2.isValidSpawn(this.level, var1, this.tamable.getType()) && this.level.isEmptyBlock(var1.above()) && this.level.isEmptyBlock(var1.above(2));
   }
}
