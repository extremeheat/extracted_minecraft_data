package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;

public class RandomLookAroundGoal extends Goal {
   private final Mob mob;
   private double relX;
   private double relZ;
   private int lookTime;

   public RandomLookAroundGoal(final Mob mob) {
      super();
      this.mob = mob;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.mob.getRandom().nextFloat() < 0.02F;
   }

   public boolean canContinueToUse() {
      return this.lookTime >= 0;
   }

   public void start() {
      double rnd = 6.283185307179586 * this.mob.getRandom().nextDouble();
      this.relX = Math.cos(rnd);
      this.relZ = Math.sin(rnd);
      this.lookTime = 20 + this.mob.getRandom().nextInt(20);
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      --this.lookTime;
      this.mob.getLookControl().setLookAt(this.mob.getX() + this.relX, this.mob.getEyeY(), this.mob.getZ() + this.relZ);
   }
}
