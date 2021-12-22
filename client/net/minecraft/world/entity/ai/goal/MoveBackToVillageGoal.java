package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveBackToVillageGoal extends RandomStrollGoal {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;

   public MoveBackToVillageGoal(PathfinderMob var1, double var2, boolean var4) {
      super(var1, var2, 10, var4);
   }

   public boolean canUse() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      BlockPos var2 = this.mob.blockPosition();
      return var1.isVillage(var2) ? false : super.canUse();
   }

   @Nullable
   protected Vec3 getPosition() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      BlockPos var2 = this.mob.blockPosition();
      SectionPos var3 = SectionPos.method_71(var2);
      SectionPos var4 = BehaviorUtils.findSectionClosestToVillage(var1, var3, 2);
      return var4 != var3 ? DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(var4.center()), 1.5707963705062866D) : null;
   }
}
