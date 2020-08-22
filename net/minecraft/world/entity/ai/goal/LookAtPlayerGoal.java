package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class LookAtPlayerGoal extends Goal {
   protected final Mob mob;
   protected Entity lookAt;
   protected final float lookDistance;
   private int lookTime;
   protected final float probability;
   protected final Class lookAtType;
   protected final TargetingConditions lookAtContext;

   public LookAtPlayerGoal(Mob var1, Class var2, float var3) {
      this(var1, var2, var3, 0.02F);
   }

   public LookAtPlayerGoal(Mob var1, Class var2, float var3, float var4) {
      this.mob = var1;
      this.lookAtType = var2;
      this.lookDistance = var3;
      this.probability = var4;
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      if (var2 == Player.class) {
         this.lookAtContext = (new TargetingConditions()).range((double)var3).allowSameTeam().allowInvulnerable().allowNonAttackable().selector((var1x) -> {
            return EntitySelector.notRiding(var1).test(var1x);
         });
      } else {
         this.lookAtContext = (new TargetingConditions()).range((double)var3).allowSameTeam().allowInvulnerable().allowNonAttackable();
      }

   }

   public boolean canUse() {
      if (this.mob.getRandom().nextFloat() >= this.probability) {
         return false;
      } else {
         if (this.mob.getTarget() != null) {
            this.lookAt = this.mob.getTarget();
         }

         if (this.lookAtType == Player.class) {
            this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
         } else {
            this.lookAt = this.mob.level.getNearestLoadedEntity(this.lookAtType, this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance));
         }

         return this.lookAt != null;
      }
   }

   public boolean canContinueToUse() {
      if (!this.lookAt.isAlive()) {
         return false;
      } else if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   public void start() {
      this.lookTime = 40 + this.mob.getRandom().nextInt(40);
   }

   public void stop() {
      this.lookAt = null;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
      --this.lookTime;
   }
}
