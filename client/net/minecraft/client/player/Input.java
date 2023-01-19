package net.minecraft.client.player;

import net.minecraft.world.phys.Vec2;

public class Input {
   public float leftImpulse;
   public float forwardImpulse;
   public boolean up;
   public boolean down;
   public boolean left;
   public boolean right;
   public boolean jumping;
   public boolean shiftKeyDown;

   public Input() {
      super();
   }

   public void tick(boolean var1, float var2) {
   }

   public Vec2 getMoveVector() {
      return new Vec2(this.leftImpulse, this.forwardImpulse);
   }

   public boolean hasForwardImpulse() {
      return this.forwardImpulse > 1.0E-5F;
   }
}
