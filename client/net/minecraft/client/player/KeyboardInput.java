package net.minecraft.client.player;

import net.minecraft.client.Options;

public class KeyboardInput extends Input {
   private final Options options;

   public KeyboardInput(Options var1) {
      super();
      this.options = var1;
   }

   private static float calculateImpulse(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }

   public void tick(boolean var1, float var2) {
      this.up = this.options.keyUp.isDown();
      this.down = this.options.keyDown.isDown();
      this.left = this.options.keyLeft.isDown();
      this.right = this.options.keyRight.isDown();
      this.forwardImpulse = calculateImpulse(this.up, this.down);
      this.leftImpulse = calculateImpulse(this.left, this.right);
      this.jumping = this.options.keyJump.isDown();
      this.shiftKeyDown = this.options.keyShift.isDown();
      if (var1) {
         this.leftImpulse *= var2;
         this.forwardImpulse *= var2;
      }

   }
}
