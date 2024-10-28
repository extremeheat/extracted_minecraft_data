package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FleeSunGoal extends Goal {
   protected final PathfinderMob mob;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;
   private final Level level;

   public FleeSunGoal(PathfinderMob var1, double var2) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.level = var1.level();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.getTarget() != null) {
         return false;
      } else if (!this.level.isDay()) {
         return false;
      } else if (!this.mob.isOnFire()) {
         return false;
      } else if (!this.level.canSeeSky(this.mob.blockPosition())) {
         return false;
      } else {
         return !this.mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? false : this.setWantedPos();
      }
   }

   protected boolean setWantedPos() {
      Vec3 var1 = this.getHidePos();
      if (var1 == null) {
         return false;
      } else {
         this.wantedX = var1.x;
         this.wantedY = var1.y;
         this.wantedZ = var1.z;
         return true;
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   @Nullable
   protected Vec3 getHidePos() {
      RandomSource var1 = this.mob.getRandom();
      BlockPos var2 = this.mob.blockPosition();

      for(int var3 = 0; var3 < 10; ++var3) {
         BlockPos var4 = var2.offset(var1.nextInt(20) - 10, var1.nextInt(6) - 3, var1.nextInt(20) - 10);
         if (!this.level.canSeeSky(var4) && this.mob.getWalkTargetValue(var4) < 0.0F) {
            return Vec3.atBottomCenterOf(var4);
         }
      }

      return null;
   }
}
