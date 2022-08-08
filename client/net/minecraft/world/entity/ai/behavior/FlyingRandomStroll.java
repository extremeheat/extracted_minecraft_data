package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3;

public class FlyingRandomStroll extends RandomStroll {
   public FlyingRandomStroll(float var1) {
      this(var1, true);
   }

   public FlyingRandomStroll(float var1, boolean var2) {
      super(var1, var2);
   }

   protected Vec3 getTargetPos(PathfinderMob var1) {
      Vec3 var2 = var1.getViewVector(0.0F);
      return AirAndWaterRandomPos.getPos(var1, this.maxHorizontalDistance, this.maxVerticalDistance, -2, var2.x, var2.z, 1.5707963705062866);
   }
}
