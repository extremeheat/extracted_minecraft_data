package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class BreedGoal extends Goal {
   private static final TargetingConditions PARTNER_TARGETING = (new TargetingConditions()).range(8.0D).allowInvulnerable().allowSameTeam().allowUnseeable();
   protected final Animal animal;
   private final Class<? extends Animal> partnerClass;
   protected final Level level;
   protected Animal partner;
   private int loveTime;
   private final double speedModifier;

   public BreedGoal(Animal var1, double var2) {
      this(var1, var2, var1.getClass());
   }

   public BreedGoal(Animal var1, double var2, Class<? extends Animal> var4) {
      super();
      this.animal = var1;
      this.level = var1.level;
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
      return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
   }

   public void stop() {
      this.partner = null;
      this.loveTime = 0;
   }

   public void tick() {
      this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
      this.animal.getNavigation().moveTo((Entity)this.partner, this.speedModifier);
      ++this.loveTime;
      if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0D) {
         this.breed();
      }

   }

   @Nullable
   private Animal getFreePartner() {
      List var1 = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
      double var2 = 1.7976931348623157E308D;
      Animal var4 = null;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Animal var6 = (Animal)var5.next();
         if (this.animal.canMate(var6) && this.animal.distanceToSqr(var6) < var2) {
            var4 = var6;
            var2 = this.animal.distanceToSqr(var6);
         }
      }

      return var4;
   }

   protected void breed() {
      AgableMob var1 = this.animal.getBreedOffspring(this.partner);
      if (var1 != null) {
         ServerPlayer var2 = this.animal.getLoveCause();
         if (var2 == null && this.partner.getLoveCause() != null) {
            var2 = this.partner.getLoveCause();
         }

         if (var2 != null) {
            var2.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(var2, this.animal, this.partner, var1);
         }

         this.animal.setAge(6000);
         this.partner.setAge(6000);
         this.animal.resetLove();
         this.partner.resetLove();
         var1.setAge(-24000);
         var1.moveTo(this.animal.x, this.animal.y, this.animal.z, 0.0F, 0.0F);
         this.level.addFreshEntity(var1);
         this.level.broadcastEntityEvent(this.animal, (byte)18);
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.x, this.animal.y, this.animal.z, this.animal.getRandom().nextInt(7) + 1));
         }

      }
   }
}
