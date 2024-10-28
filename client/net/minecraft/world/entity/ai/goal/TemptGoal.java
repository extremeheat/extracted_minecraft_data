package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TemptGoal extends Goal {
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
   private final TargetingConditions targetingConditions;
   protected final PathfinderMob mob;
   private final double speedModifier;
   private double px;
   private double py;
   private double pz;
   private double pRotX;
   private double pRotY;
   @Nullable
   protected Player player;
   private int calmDown;
   private boolean isRunning;
   private final Predicate<ItemStack> items;
   private final boolean canScare;

   public TemptGoal(PathfinderMob var1, double var2, Predicate<ItemStack> var4, boolean var5) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.items = var4;
      this.canScare = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetingConditions = TEMPT_TARGETING.copy().selector((var1x, var2x) -> {
         return this.shouldFollow(var1x);
      });
   }

   public boolean canUse() {
      if (this.calmDown > 0) {
         --this.calmDown;
         return false;
      } else {
         this.player = getServerLevel(this.mob).getNearestPlayer(this.targetingConditions.range(this.mob.getAttributeValue(Attributes.TEMPT_RANGE)), this.mob);
         return this.player != null;
      }
   }

   private boolean shouldFollow(LivingEntity var1) {
      return this.items.test(var1.getMainHandItem()) || this.items.test(var1.getOffhandItem());
   }

   public boolean canContinueToUse() {
      if (this.canScare()) {
         if (this.mob.distanceToSqr(this.player) < 36.0) {
            if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs((double)this.player.getXRot() - this.pRotX) > 5.0 || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0) {
               return false;
            }
         } else {
            this.px = this.player.getX();
            this.py = this.player.getY();
            this.pz = this.player.getZ();
         }

         this.pRotX = (double)this.player.getXRot();
         this.pRotY = (double)this.player.getYRot();
      }

      return this.canUse();
   }

   protected boolean canScare() {
      return this.canScare;
   }

   public void start() {
      this.px = this.player.getX();
      this.py = this.player.getY();
      this.pz = this.player.getZ();
      this.isRunning = true;
   }

   public void stop() {
      this.player = null;
      this.mob.getNavigation().stop();
      this.calmDown = reducedTickDelay(100);
      this.isRunning = false;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
      if (this.mob.distanceToSqr(this.player) < 6.25) {
         this.mob.getNavigation().stop();
      } else {
         this.mob.getNavigation().moveTo((Entity)this.player, this.speedModifier);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
