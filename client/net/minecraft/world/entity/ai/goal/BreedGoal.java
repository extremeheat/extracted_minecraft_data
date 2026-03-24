package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;

public class BreedGoal extends Goal {
   private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
   protected final Animal animal;
   private final Class<? extends Animal> partnerClass;
   protected final ServerLevel level;
   protected @Nullable Animal partner;
   private int loveTime;
   private final double speedModifier;

   public BreedGoal(final Animal animal, final double speedModifier) {
      this(animal, speedModifier, animal.getClass());
   }

   public BreedGoal(final Animal animal, final double speedModifier, final Class<? extends Animal> clazz) {
      super();
      this.animal = animal;
      this.level = getServerLevel(animal);
      this.partnerClass = clazz;
      this.speedModifier = speedModifier;
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

   private @Nullable Animal getFreePartner() {
      List<? extends Animal> animals = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
      double dist = 1.7976931348623157E308;
      Animal partner = null;

      for(Animal potentialPartner : animals) {
         if (this.animal.canMate(potentialPartner) && !potentialPartner.isPanicking() && this.animal.distanceToSqr(potentialPartner) < dist) {
            partner = potentialPartner;
            dist = this.animal.distanceToSqr(potentialPartner);
         }
      }

      return partner;
   }

   protected void breed() {
      this.animal.spawnChildFromBreeding(this.level, this.partner);
   }
}
