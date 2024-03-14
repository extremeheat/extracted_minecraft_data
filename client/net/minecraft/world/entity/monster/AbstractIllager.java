package net.minecraft.world.entity.monster;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllager extends Raider {
   protected AbstractIllager(EntityType<? extends AbstractIllager> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   @Override
   public boolean canAttack(LivingEntity var1) {
      return var1 instanceof AbstractVillager && var1.isBaby() ? false : super.canAttack(var1);
   }

   @Override
   public boolean isAlliedTo(Entity var1) {
      if (super.isAlliedTo(var1)) {
         return true;
      } else if (!var1.getType().is(EntityTypeTags.ILLAGER_FRIENDS)) {
         return false;
      } else {
         return this.getTeam() == null && var1.getTeam() == null;
      }
   }

   public static enum IllagerArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW,
      CROSSBOW_HOLD,
      CROSSBOW_CHARGE,
      CELEBRATING,
      NEUTRAL;

      private IllagerArmPose() {
      }
   }

   protected class RaiderOpenDoorGoal extends OpenDoorGoal {
      public RaiderOpenDoorGoal(Raider var2) {
         super(var2, false);
      }

      @Override
      public boolean canUse() {
         return super.canUse() && AbstractIllager.this.hasActiveRaid();
      }
   }
}
