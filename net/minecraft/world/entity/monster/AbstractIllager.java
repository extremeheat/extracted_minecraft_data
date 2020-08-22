package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllager extends Raider {
   protected AbstractIllager(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public MobType getMobType() {
      return MobType.ILLAGER;
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   public class RaiderOpenDoorGoal extends OpenDoorGoal {
      public RaiderOpenDoorGoal(Raider var2) {
         super(var2, false);
      }

      public boolean canUse() {
         return super.canUse() && AbstractIllager.this.hasActiveRaid();
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
   }
}
