package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class StrollThroughVillageGoal extends Goal {
   private final PathfinderMob mob;
   private final int interval;
   @Nullable
   private BlockPos wantedPos;

   public StrollThroughVillageGoal(PathfinderMob var1, int var2) {
      super();
      this.mob = var1;
      this.interval = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else if (this.mob.level.isDay()) {
         return false;
      } else if (this.mob.getRandom().nextInt(this.interval) != 0) {
         return false;
      } else {
         ServerLevel var1 = (ServerLevel)this.mob.level;
         BlockPos var2 = this.mob.blockPosition();
         if (!var1.isCloseToVillage(var2, 6)) {
            return false;
         } else {
            Vec3 var3 = LandRandomPos.getPos(this.mob, 15, 7, (var1x) -> {
               return (double)(-var1.sectionsToVillage(SectionPos.of(var1x)));
            });
            this.wantedPos = var3 == null ? null : new BlockPos(var3);
            return this.wantedPos != null;
         }
      }
   }

   public boolean canContinueToUse() {
      return this.wantedPos != null && !this.mob.getNavigation().isDone() && this.mob.getNavigation().getTargetPos().equals(this.wantedPos);
   }

   public void tick() {
      if (this.wantedPos != null) {
         PathNavigation var1 = this.mob.getNavigation();
         if (var1.isDone() && !this.wantedPos.closerThan(this.mob.position(), 10.0D)) {
            Vec3 var2 = Vec3.atBottomCenterOf(this.wantedPos);
            Vec3 var3 = this.mob.position();
            Vec3 var4 = var3.subtract(var2);
            var2 = var4.scale(0.4D).add(var2);
            Vec3 var5 = var2.subtract(var3).normalize().scale(10.0D).add(var3);
            BlockPos var6 = new BlockPos(var5);
            var6 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var6);
            if (!var1.moveTo((double)var6.getX(), (double)var6.getY(), (double)var6.getZ(), 1.0D)) {
               this.moveRandomly();
            }
         }

      }
   }

   private void moveRandomly() {
      Random var1 = this.mob.getRandom();
      BlockPos var2 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + var1.nextInt(16), 0, -8 + var1.nextInt(16)));
      this.mob.getNavigation().moveTo((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), 1.0D);
   }
}
