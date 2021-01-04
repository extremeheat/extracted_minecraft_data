package net.minecraft.client.player;

import net.minecraft.client.Options;

public class KeyboardInput extends Input {
   private final Options options;

   public KeyboardInput(Options var1) {
      super();
      this.options = var1;
   }

   public void tick(boolean var1, boolean var2) {
      this.up = this.options.keyUp.isDown();
      this.down = this.options.keyDown.isDown();
      this.left = this.options.keyLeft.isDown();
      this.right = this.options.keyRight.isDown();
      this.forwardImpulse = this.up == this.down ? 0.0F : (float)(this.up ? 1 : -1);
      this.leftImpulse = this.left == this.right ? 0.0F : (float)(this.left ? 1 : -1);
      this.jumping = this.options.keyJump.isDown();
      this.sneakKeyDown = this.options.keySneak.isDown();
      if (!var2 && (this.sneakKeyDown || var1)) {
         this.leftImpulse = (float)((double)this.leftImpulse * 0.3D);
         this.forwardImpulse = (float)((double)this.forwardImpulse * 0.3D);
      }

   }
}
