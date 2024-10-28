package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class OwnerHurtByTargetGoal extends TargetGoal {
   private final TamableAnimal tameAnimal;
   private LivingEntity ownerLastHurtBy;
   private int timestamp;

   public OwnerHurtByTargetGoal(TamableAnimal var1) {
      super(var1, false);
      this.tameAnimal = var1;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit()) {
         LivingEntity var1 = this.tameAnimal.getOwner();
         if (var1 == null) {
            return false;
         } else {
            this.ownerLastHurtBy = var1.getLastHurtByMob();
            int var2 = var1.getLastHurtByMobTimestamp();
            return var2 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, var1);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurtBy);
      LivingEntity var1 = this.tameAnimal.getOwner();
      if (var1 != null) {
         this.timestamp = var1.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}
