package net.minecraft.world.entity;

public interface PlayerRideableJumping {
   boolean canJump();

   void handleStartJump(int var1);

   void handleStopJump();
}
