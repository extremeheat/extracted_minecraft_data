package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TemptGoal extends Goal {
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
   private static final double DEFAULT_STOP_DISTANCE = 2.5;
   private final TargetingConditions targetingConditions;
   protected final Mob mob;
   protected final double speedModifier;
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
   private final double stopDistance;

   public TemptGoal(PathfinderMob var1, double var2, Predicate<ItemStack> var4, boolean var5) {
      this((Mob)var1, var2, var4, var5, 2.5);
   }

   public TemptGoal(PathfinderMob var1, double var2, Predicate<ItemStack> var4, boolean var5, double var6) {
      this((Mob)var1, var2, var4, var5, var6);
   }

   TemptGoal(Mob var1, double var2, Predicate<ItemStack> var4, boolean var5, double var6) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.items = var4;
      this.canScare = var5;
      this.stopDistance = var6;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetingConditions = TEMPT_TARGETING.copy().selector((var1x, var2x) -> this.shouldFollow(var1x));
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
      this.stopNavigation();
      this.calmDown = reducedTickDelay(100);
      this.isRunning = false;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
      if (this.mob.distanceToSqr(this.player) < this.stopDistance * this.stopDistance) {
         this.stopNavigation();
      } else {
         this.navigateTowards(this.player);
      }

   }

   protected void stopNavigation() {
      this.mob.getNavigation().stop();
   }

   protected void navigateTowards(Player var1) {
      this.mob.getNavigation().moveTo((Entity)var1, this.speedModifier);
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public static class ForNonPathfinders extends TemptGoal {
      public ForNonPathfinders(Mob var1, double var2, Predicate<ItemStack> var4, boolean var5, double var6) {
         super(var1, var2, var4, var5, var6);
      }

      protected void stopNavigation() {
         this.mob.getMoveControl().setWait();
      }

      protected void navigateTowards(Player var1) {
         Vec3 var2 = var1.getEyePosition().subtract(this.mob.position()).scale(this.mob.getRandom().nextDouble()).add(this.mob.position());
         this.mob.getMoveControl().setWantedPosition(var2.x, var2.y, var2.z, this.speedModifier);
      }
   }
}
