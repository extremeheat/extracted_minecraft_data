package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveBackToVillage extends RandomStrollGoal {
   public MoveBackToVillage(PathfinderMob var1, double var2) {
      super(var1, var2, 10);
   }

   public boolean canUse() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      BlockPos var2 = new BlockPos(this.mob);
      return var1.isVillage(var2) ? false : super.canUse();
   }

   @Nullable
   protected Vec3 getPosition() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      BlockPos var2 = new BlockPos(this.mob);
      SectionPos var3 = SectionPos.of(var2);
      SectionPos var4 = BehaviorUtils.findSectionClosestToVillage(var1, var3, 2);
      return var4 != var3 ? RandomPos.getPosTowards(this.mob, 10, 7, new Vec3(var4.center())) : null;
   }
}
