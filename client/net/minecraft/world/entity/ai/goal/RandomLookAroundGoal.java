package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;

public class RandomLookAroundGoal extends Goal {
   private final Mob mob;
   private double relX;
   private double relZ;
   private int lookTime;

   public RandomLookAroundGoal(Mob var1) {
      super();
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.mob.getRandom().nextFloat() < 0.02F;
   }

   public boolean canContinueToUse() {
      return this.lookTime >= 0;
   }

   public void start() {
      double var1 = 6.283185307179586D * this.mob.getRandom().nextDouble();
      this.relX = Math.cos(var1);
      this.relZ = Math.sin(var1);
      this.lookTime = 20 + this.mob.getRandom().nextInt(20);
   }

   public void tick() {
      --this.lookTime;
      this.mob.getLookControl().setLookAt(this.mob.x + this.relX, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z + this.relZ);
   }
}
