package net.minecraft.client.player;

import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class ClientInput {
   public Input keyPresses;
   protected Vec2 moveVector;

   public ClientInput() {
      super();
      this.keyPresses = Input.EMPTY;
      this.moveVector = Vec2.ZERO;
   }

   public void tick() {
   }

   public Vec2 getMoveVector() {
      return this.moveVector;
   }

   public void scaleMoveDirection(float var1) {
      this.moveVector = this.moveVector.scale(var1);
   }

   public boolean hasForwardImpulse() {
      return this.moveVector.y > 1.0E-5F;
   }

   public void makeJump() {
      this.keyPresses = new Input(this.keyPresses.forward(), this.keyPresses.backward(), this.keyPresses.left(), this.keyPresses.right(), true, this.keyPresses.shift(), this.keyPresses.sprint());
   }
}
