package net.minecraft.client.player;

import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

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

   public void tick() {
      this.keyPresses = new Input(this.options.keyUp.isDown(), this.options.keyDown.isDown(), this.options.keyLeft.isDown(), this.options.keyRight.isDown(), this.options.keyJump.isDown(), this.options.keyShift.isDown(), this.options.keySprint.isDown());
      float var1 = calculateImpulse(this.keyPresses.forward(), this.keyPresses.backward());
      float var2 = calculateImpulse(this.keyPresses.left(), this.keyPresses.right());
      this.moveVector = (new Vec2(var2, var1)).normalized();
   }
}
