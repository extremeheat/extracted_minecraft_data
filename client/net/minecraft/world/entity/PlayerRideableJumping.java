package net.minecraft.world.entity;

public interface PlayerRideableJumping {
   void onPlayerJump(int var1);

   boolean canJump();

   void handleStartJump(int var1);

   void handleStopJump();
}
