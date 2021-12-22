package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class TemptGoal extends Goal {
   private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
   private final TargetingConditions targetingConditions;
   protected final PathfinderMob mob;
   private final double speedModifier;
   // $FF: renamed from: px double
   private double field_7;
   // $FF: renamed from: py double
   private double field_8;
   // $FF: renamed from: pz double
   private double field_9;
   private double pRotX;
   private double pRotY;
   @Nullable
   protected Player player;
   private int calmDown;
   private boolean isRunning;
   private final Ingredient items;
   private final boolean canScare;

   public TemptGoal(PathfinderMob var1, double var2, Ingredient var4, boolean var5) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.items = var4;
      this.canScare = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
   }

   public boolean canUse() {
      if (this.calmDown > 0) {
         --this.calmDown;
         return false;
      } else {
         this.player = this.mob.level.getNearestPlayer(this.targetingConditions, this.mob);
         return this.player != null;
      }
   }

   private boolean shouldFollow(LivingEntity var1) {
      return this.items.test(var1.getMainHandItem()) || this.items.test(var1.getOffhandItem());
   }

   public boolean canContinueToUse() {
      if (this.canScare()) {
         if (this.mob.distanceToSqr(this.player) < 36.0D) {
            if (this.player.distanceToSqr(this.field_7, this.field_8, this.field_9) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.player.getXRot() - this.pRotX) > 5.0D || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0D) {
               return false;
            }
         } else {
            this.field_7 = this.player.getX();
            this.field_8 = this.player.getY();
            this.field_9 = this.player.getZ();
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
      this.field_7 = this.player.getX();
      this.field_8 = this.player.getY();
      this.field_9 = this.player.getZ();
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
      if (this.mob.distanceToSqr(this.player) < 6.25D) {
         this.mob.getNavigation().stop();
      } else {
         this.mob.getNavigation().moveTo((Entity)this.player, this.speedModifier);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
