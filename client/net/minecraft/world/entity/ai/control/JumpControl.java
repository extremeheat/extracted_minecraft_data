package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.Mob;

public class JumpControl implements Control {
   private final Mob mob;
   protected boolean jump;

   public JumpControl(Mob var1) {
      super();
      this.mob = var1;
   }

   public void jump() {
      this.jump = true;
   }

   public void tick() {
      this.mob.setJumping(this.jump);
      this.jump = false;
   }
}
