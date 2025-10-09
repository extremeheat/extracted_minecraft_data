package net.minecraft.world.entity;

public interface PlayerRideableJumping extends PlayerRideable {
   void onPlayerJump(int var1);

   boolean canJump();

   void handleStartJump(int var1);

   void handleStopJump();

   default int getJumpCooldown() {
      return 0;
   }

   default float getPlayerJumpPendingScale(int var1) {
      return var1 >= 90 ? 1.0F : 0.4F + 0.4F * (float)var1 / 90.0F;
   }
}
