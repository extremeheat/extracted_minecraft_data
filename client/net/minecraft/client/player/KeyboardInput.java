package net.minecraft.client.player;

import net.minecraft.client.Options;

public class KeyboardInput extends Input {
   private final Options options;
   private static final double MOVING_SLOW_FACTOR = 0.3D;

   public KeyboardInput(Options var1) {
      super();
      this.options = var1;
   }

   public void tick(boolean var1) {
      this.up = this.options.keyUp.isDown();
      this.down = this.options.keyDown.isDown();
      this.left = this.options.keyLeft.isDown();
      this.right = this.options.keyRight.isDown();
      this.forwardImpulse = this.up == this.down ? 0.0F : (this.up ? 1.0F : -1.0F);
      this.leftImpulse = this.left == this.right ? 0.0F : (this.left ? 1.0F : -1.0F);
      this.jumping = this.options.keyJump.isDown();
      this.shiftKeyDown = this.options.keyShift.isDown();
      if (var1) {
         this.leftImpulse = (float)((double)this.leftImpulse * 0.3D);
         this.forwardImpulse = (float)((double)this.forwardImpulse * 0.3D);
      }

   }
}
