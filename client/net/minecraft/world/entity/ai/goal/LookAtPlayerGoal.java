package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class LookAtPlayerGoal extends Goal {
   public static final float DEFAULT_PROBABILITY = 0.02F;
   protected final Mob mob;
   @Nullable
   protected Entity lookAt;
   protected final float lookDistance;
   private int lookTime;
   protected final float probability;
   private final boolean onlyHorizontal;
   protected final Class<? extends LivingEntity> lookAtType;
   protected final TargetingConditions lookAtContext;

   public LookAtPlayerGoal(Mob var1, Class<? extends LivingEntity> var2, float var3) {
      this(var1, var2, var3, 0.02F);
   }

   public LookAtPlayerGoal(Mob var1, Class<? extends LivingEntity> var2, float var3, float var4) {
      this(var1, var2, var3, var4, false);
   }

   public LookAtPlayerGoal(Mob var1, Class<? extends LivingEntity> var2, float var3, float var4, boolean var5) {
      super();
      this.mob = var1;
      this.lookAtType = var2;
      this.lookDistance = var3;
      this.probability = var4;
      this.onlyHorizontal = var5;
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      if (var2 == Player.class) {
         this.lookAtContext = TargetingConditions.forNonCombat().range((double)var3).selector((var1x) -> {
            return EntitySelector.notRiding(var1).test(var1x);
         });
      } else {
         this.lookAtContext = TargetingConditions.forNonCombat().range((double)var3);
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
            this.lookAt = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0, (double)this.lookDistance), (var0) -> {
               return true;
            }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
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
      this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
   }

   public void stop() {
      this.lookAt = null;
   }

   public void tick() {
      if (this.lookAt.isAlive()) {
         double var1 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
         this.mob.getLookControl().setLookAt(this.lookAt.getX(), var1, this.lookAt.getZ());
         --this.lookTime;
      }
   }
}
