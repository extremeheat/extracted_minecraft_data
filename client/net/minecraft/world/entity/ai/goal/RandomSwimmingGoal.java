package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.phys.Vec3;

public class RandomSwimmingGoal extends RandomStrollGoal {
   public RandomSwimmingGoal(PathfinderMob var1, double var2, int var4) {
      super(var1, var2, var4);
   }

   @Nullable
   protected Vec3 getPosition() {
      return BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7);
   }
}
