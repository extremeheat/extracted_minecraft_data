package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;

public class BreedGoal extends Goal {
   private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
   protected final Animal animal;
   private final Class<? extends Animal> partnerClass;
   protected final ServerLevel level;
   @Nullable
   protected Animal partner;
   private int loveTime;
   private final double speedModifier;

   public BreedGoal(Animal var1, double var2) {
      this(var1, var2, var1.getClass());
   }

   public BreedGoal(Animal var1, double var2, Class<? extends Animal> var4) {
      super();
      this.animal = var1;
      this.level = getServerLevel(var1);
      this.partnerClass = var4;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.partner = this.getFreePartner();
         return this.partner != null;
      }
   }

   public boolean canContinueToUse() {
      return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 && !this.partner.isPanicking();
   }

   public void stop() {
      this.partner = null;
      this.loveTime = 0;
   }

   public void tick() {
      this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
      this.animal.getNavigation().moveTo((Entity)this.partner, this.speedModifier);
      ++this.loveTime;
      if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0) {
         this.breed();
      }

   }

   @Nullable
   private Animal getFreePartner() {
      List var1 = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
      double var2 = 1.7976931348623157E308;
      Animal var4 = null;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Animal var6 = (Animal)var5.next();
         if (this.animal.canMate(var6) && !var6.isPanicking() && this.animal.distanceToSqr(var6) < var2) {
            var4 = var6;
            var2 = this.animal.distanceToSqr(var6);
         }
      }

      return var4;
   }

   protected void breed() {
      this.animal.spawnChildFromBreeding(this.level, this.partner);
   }
}
