package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.DimensionTransition;

public class PortalProcessor {
   private Portal portal;
   private BlockPos entryPosition;
   private int portalTime;
   private boolean insidePortalThisTick;

   public PortalProcessor(Portal var1, BlockPos var2) {
      super();
      this.portal = var1;
      this.entryPosition = var2;
      this.insidePortalThisTick = true;
   }

   public boolean processPortalTeleportation(ServerLevel var1, Entity var2, boolean var3) {
      if (!this.insidePortalThisTick) {
         this.decayTick();
         return false;
      } else {
         this.insidePortalThisTick = false;
         return var3 && this.portalTime++ >= this.portal.getPortalTransitionTime(var1, var2);
      }
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel var1, Entity var2) {
      return this.portal.getPortalDestination(var1, var2, this.entryPosition);
   }

   public Portal.Transition getPortalLocalTransition() {
      return this.portal.getLocalTransition();
   }

   private void decayTick() {
      this.portalTime = Math.max(this.portalTime - 4, 0);
   }

   public boolean hasExpired() {
      return this.portalTime <= 0;
   }

   public BlockPos getEntryPosition() {
      return this.entryPosition;
   }

   public void updateEntryPosition(BlockPos var1) {
      this.entryPosition = var1;
   }

   public int getPortalTime() {
      return this.portalTime;
   }

   public boolean isInsidePortalThisTick() {
      return this.insidePortalThisTick;
   }

   public void setAsInsidePortalThisTick(boolean var1) {
      this.insidePortalThisTick = var1;
   }

   public boolean isSamePortal(Portal var1) {
      return this.portal == var1;
   }
}
