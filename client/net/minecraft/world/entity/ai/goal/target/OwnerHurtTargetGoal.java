package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class OwnerHurtTargetGoal extends TargetGoal {
   private final TamableAnimal tameAnimal;
   private LivingEntity ownerLastHurt;
   private int timestamp;

   public OwnerHurtTargetGoal(TamableAnimal var1) {
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
            this.ownerLastHurt = var1.getLastHurtMob();
            int var2 = var1.getLastHurtMobTimestamp();
            return var2 != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurt, var1);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurt);
      LivingEntity var1 = this.tameAnimal.getOwner();
      if (var1 != null) {
         this.timestamp = var1.getLastHurtMobTimestamp();
      }

      super.start();
   }
}
