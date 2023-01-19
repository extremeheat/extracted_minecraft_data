package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomStrollGoal {
   public WaterAvoidingRandomFlyingGoal(PathfinderMob var1, double var2) {
      super(var1, var2);
   }

   @Nullable
   @Override
   protected Vec3 getPosition() {
      Vec3 var1 = this.mob.getViewVector(0.0F);
      boolean var2 = true;
      Vec3 var3 = HoverRandomPos.getPos(this.mob, 8, 7, var1.x, var1.z, 1.5707964F, 3, 1);
      return var3 != null ? var3 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, var1.x, var1.z, 1.5707963705062866);
   }
}
