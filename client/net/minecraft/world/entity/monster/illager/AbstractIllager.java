package net.minecraft.world.entity.monster.illager;

import java.util.Objects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllager extends Raider {
   protected AbstractIllager(final EntityType<? extends AbstractIllager> type, final Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public IllagerArmPose getArmPose() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   public boolean canAttack(final Entity target) {
      if (target instanceof AbstractVillager villager) {
         if (villager.isBaby()) {
            return false;
         }
      }

      return super.canAttack(target);
   }

   protected boolean considersEntityAsAlly(final Entity other) {
      if (super.considersEntityAsAlly(other)) {
         return true;
      } else if (!other.is(EntityTypeTags.ILLAGER_FRIENDS)) {
         return false;
      } else {
         return this.getTeam() == null && other.getTeam() == null;
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

      // $FF: synthetic method
      private static IllagerArmPose[] $values() {
         return new IllagerArmPose[]{CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL};
      }
   }

   protected class RaiderOpenDoorGoal extends OpenDoorGoal {
      public RaiderOpenDoorGoal(final Raider raider) {
         Objects.requireNonNull(AbstractIllager.this);
         super(raider, false);
      }

      public boolean canUse() {
         return super.canUse() && AbstractIllager.this.hasActiveRaid();
      }
   }
}
