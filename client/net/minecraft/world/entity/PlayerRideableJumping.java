package net.minecraft.world.entity;

import net.minecraft.world.entity.player.Player;

public interface PlayerRideableJumping extends PlayerRideable {
   void onPlayerJump(int var1);

   boolean canJump(Player var1);

   void handleStartJump(int var1);

   void handleStopJump();

   default int getJumpCooldown() {
      return 0;
   }
}
