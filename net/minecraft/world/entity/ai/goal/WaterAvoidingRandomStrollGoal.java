package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomStrollGoal extends RandomStrollGoal {
   protected final float probability;

   public WaterAvoidingRandomStrollGoal(PathfinderMob var1, double var2) {
      this(var1, var2, 0.001F);
   }

   public WaterAvoidingRandomStrollGoal(PathfinderMob var1, double var2, float var4) {
      super(var1, var2);
      this.probability = var4;
   }

   @Nullable
   protected Vec3 getPosition() {
      if (this.mob.isInWaterOrBubble()) {
         Vec3 var1 = RandomPos.getLandPos(this.mob, 15, 7);
         return var1 == null ? super.getPosition() : var1;
      } else {
         return this.mob.getRandom().nextFloat() >= this.probability ? RandomPos.getLandPos(this.mob, 10, 7) : super.getPosition();
      }
   }
}
