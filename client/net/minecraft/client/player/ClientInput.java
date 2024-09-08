package net.minecraft.client.player;

import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class ClientInput {
   public Input keyPresses = Input.EMPTY;
   public float leftImpulse;
   public float forwardImpulse;

   public ClientInput() {
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

   public void makeJump() {
      this.keyPresses = new Input(
         this.keyPresses.forward(),
         this.keyPresses.backward(),
         this.keyPresses.left(),
         this.keyPresses.right(),
         true,
         this.keyPresses.shift(),
         this.keyPresses.sprint()
      );
   }
}
