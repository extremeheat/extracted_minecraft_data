package net.minecraft.client.player;

import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Input;

public class KeyboardInput extends ClientInput {
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
      this.keyPresses = new Input(this.options.keyUp.isDown(), this.options.keyDown.isDown(), this.options.keyLeft.isDown(), this.options.keyRight.isDown(), this.options.keyJump.isDown(), this.options.keyShift.isDown(), this.options.keySprint.isDown());
      this.forwardImpulse = calculateImpulse(this.keyPresses.forward(), this.keyPresses.backward());
      this.leftImpulse = calculateImpulse(this.keyPresses.left(), this.keyPresses.right());
      if (var1) {
         this.leftImpulse *= var2;
         this.forwardImpulse *= var2;
      }

   }
}
