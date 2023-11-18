package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class HurtByTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
   private static final int ALERT_RANGE_Y = 10;
   private boolean alertSameType;
   private int timestamp;
   private final Class<?>[] toIgnoreDamage;
   @Nullable
   private Class<?>[] toIgnoreAlert;

   public HurtByTargetGoal(PathfinderMob var1, Class<?>... var2) {
      super(var1, true);
      this.toIgnoreDamage = var2;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      int var1 = this.mob.getLastHurtByMobTimestamp();
      LivingEntity var2 = this.mob.getLastHurtByMob();
      if (var1 != this.timestamp && var2 != null) {
         if (var2.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            return false;
         } else {
            for(Class var6 : this.toIgnoreDamage) {
               if (var6.isAssignableFrom(var2.getClass())) {
                  return false;
               }
            }

            return this.canAttack(var2, HURT_BY_TARGETING);
         }
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setAlertOthers(Class<?>... var1) {
      this.alertSameType = true;
      this.toIgnoreAlert = var1;
      return this;
   }

   @Override
   public void start() {
      this.mob.setTarget(this.mob.getLastHurtByMob());
      this.targetMob = this.mob.getTarget();
      this.timestamp = this.mob.getLastHurtByMobTimestamp();
      this.unseenMemoryTicks = 300;
      if (this.alertSameType) {
         this.alertOthers();
      }

      super.start();
   }

   protected void alertOthers() {
      double var1 = this.getFollowDistance();
      AABB var3 = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(var1, 10.0, var1);
      List var4 = this.mob.level().getEntitiesOfClass(this.mob.getClass(), var3, EntitySelector.NO_SPECTATORS);
      Iterator var5 = var4.iterator();

      while(true) {
         Mob var6;
         while(true) {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (Mob)var5.next();
            if (this.mob != var6
               && var6.getTarget() == null
               && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal)this.mob).getOwner() == ((TamableAnimal)var6).getOwner())
               && !var6.isAlliedTo(this.mob.getLastHurtByMob())) {
               if (this.toIgnoreAlert == null) {
                  break;
               }

               boolean var7 = false;

               for(Class var11 : this.toIgnoreAlert) {
                  if (var6.getClass() == var11) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  break;
               }
            }
         }

         this.alertOther(var6, this.mob.getLastHurtByMob());
      }
   }

   protected void alertOther(Mob var1, LivingEntity var2) {
      var1.setTarget(var2);
   }
}
