package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BegGoal extends Goal {
   private final Wolf wolf;
   @Nullable
   private Player player;
   private final Level level;
   private final float lookDistance;
   private int lookTime;
   private final TargetingConditions begTargeting;

   public BegGoal(Wolf var1, float var2) {
      super();
      this.wolf = var1;
      this.level = var1.level();
      this.lookDistance = var2;
      this.begTargeting = TargetingConditions.forNonCombat().range((double)var2);
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
   }

   public boolean canUse() {
      this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
      return this.player == null ? false : this.playerHoldingInteresting(this.player);
   }

   public boolean canContinueToUse() {
      if (!this.player.isAlive()) {
         return false;
      } else if (this.wolf.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance)) {
         return false;
      } else {
         return this.lookTime > 0 && this.playerHoldingInteresting(this.player);
      }
   }

   public void start() {
      this.wolf.setIsInterested(true);
      this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
   }

   public void stop() {
      this.wolf.setIsInterested(false);
      this.player = null;
   }

   public void tick() {
      this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.wolf.getMaxHeadXRot());
      --this.lookTime;
   }

   private boolean playerHoldingInteresting(Player var1) {
      InteractionHand[] var2 = InteractionHand.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         InteractionHand var5 = var2[var4];
         ItemStack var6 = var1.getItemInHand(var5);
         if (var6.is(Items.BONE) || this.wolf.isFood(var6)) {
            return true;
         }
      }

      return false;
   }
}
