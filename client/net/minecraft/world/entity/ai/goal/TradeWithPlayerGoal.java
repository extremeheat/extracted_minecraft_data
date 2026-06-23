package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class TradeWithPlayerGoal extends Goal {
   private final AbstractVillager mob;

   public TradeWithPlayerGoal(final AbstractVillager mob) {
      super();
      this.mob = mob;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.mob.isAlive()) {
         return false;
      } else if (this.mob.isInWater()) {
         return false;
      } else if (!this.mob.onGround()) {
         return false;
      } else if (this.mob.wasHurtRecently()) {
         return false;
      } else {
         Player trader = this.mob.getTradingPlayer();
         if (trader == null) {
            return false;
         } else {
            return !(this.mob.distanceToSqr(trader) > 16.0);
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.mob.setTradingPlayer((Player)null);
   }
}
