package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class HurtByTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
   private static final int ALERT_RANGE_Y = 10;
   private boolean alertSameType;
   private int timestamp;
   private final Class<?>[] toIgnoreDamage;
   private Class<?> @Nullable [] toIgnoreAlert;

   public HurtByTargetGoal(final PathfinderMob mob, final Class<?>... ignoreDamageFromTheseTypes) {
      super(mob, true);
      this.toIgnoreDamage = ignoreDamageFromTheseTypes;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      int timestamp = this.mob.getLastHurtByMobTimestamp();
      LivingEntity lastHurtByMob = this.mob.getLastHurtByMob();
      if (timestamp != this.timestamp && lastHurtByMob != null) {
         if (lastHurtByMob.is(EntityTypes.PLAYER) && (Boolean)getServerLevel(this.mob).getGameRules().get(GameRules.UNIVERSAL_ANGER)) {
            return false;
         } else {
            for(Class<?> ignoreClass : this.toIgnoreDamage) {
               if (ignoreClass.isAssignableFrom(lastHurtByMob.getClass())) {
                  return false;
               }
            }

            return this.canAttack(lastHurtByMob, HURT_BY_TARGETING);
         }
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setAlertOthers(final Class<?>... exceptTheseTypes) {
      this.alertSameType = true;
      this.toIgnoreAlert = exceptTheseTypes;
      return this;
   }

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
      double within = this.getFollowDistance();
      AABB searchAabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(within, 10.0, within);
      List<? extends Mob> nearby = this.mob.level().getEntitiesOfClass(this.mob.getClass(), searchAabb, EntitySelector.NO_SPECTATORS);
      Iterator var5 = nearby.iterator();

      while(true) {
         Mob other;
         while(true) {
            while(true) {
               if (!var5.hasNext()) {
                  return;
               }

               other = (Mob)var5.next();
               if (this.mob != other && other.getTarget() == null) {
                  Mob var8 = this.mob;
                  if (!(var8 instanceof TamableAnimal)) {
                     break;
                  }

                  TamableAnimal tamableAnimal = (TamableAnimal)var8;
                  if (tamableAnimal.getOwner() == ((TamableAnimal)other).getOwner()) {
                     break;
                  }
               }
            }

            if (!other.isAlliedTo(this.mob.getLastHurtByMob())) {
               if (this.toIgnoreAlert == null) {
                  break;
               }

               boolean ignore = false;

               for(Class<?> ignoreClass : this.toIgnoreAlert) {
                  if (other.getClass() == ignoreClass) {
                     ignore = true;
                     break;
                  }
               }

               if (!ignore) {
                  break;
               }
            }
         }

         this.alertOther(other, this.mob.getLastHurtByMob());
      }
   }

   protected void alertOther(final Mob other, final LivingEntity hurtByMob) {
      other.setTarget(hurtByMob);
   }
}
