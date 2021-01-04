package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;

public abstract class JumpGoal extends Goal {
   public JumpGoal() {
      super();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   protected float rotlerp(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }
}
