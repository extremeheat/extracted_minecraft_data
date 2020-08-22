package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

public class HurtByTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = (new TargetingConditions()).allowUnseeable().ignoreInvisibilityTesting();
   private boolean alertSameType;
   private int timestamp;
   private final Class[] toIgnoreDamage;
   private Class[] toIgnoreAlert;

   public HurtByTargetGoal(PathfinderMob var1, Class... var2) {
      super(var1, true);
      this.toIgnoreDamage = var2;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      int var1 = this.mob.getLastHurtByMobTimestamp();
      LivingEntity var2 = this.mob.getLastHurtByMob();
      if (var1 != this.timestamp && var2 != null) {
         Class[] var3 = this.toIgnoreDamage;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class var6 = var3[var5];
            if (var6.isAssignableFrom(var2.getClass())) {
               return false;
            }
         }

         return this.canAttack(var2, HURT_BY_TARGETING);
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setAlertOthers(Class... var1) {
      this.alertSameType = true;
      this.toIgnoreAlert = var1;
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
      double var1 = this.getFollowDistance();
      List var3 = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), (new AABB(this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getX() + 1.0D, this.mob.getY() + 1.0D, this.mob.getZ() + 1.0D)).inflate(var1, 10.0D, var1));
      Iterator var4 = var3.iterator();

      while(true) {
         Mob var5;
         boolean var6;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return;
                        }

                        var5 = (Mob)var4.next();
                     } while(this.mob == var5);
                  } while(var5.getTarget() != null);
               } while(this.mob instanceof TamableAnimal && ((TamableAnimal)this.mob).getOwner() != ((TamableAnimal)var5).getOwner());
            } while(var5.isAlliedTo(this.mob.getLastHurtByMob()));

            if (this.toIgnoreAlert == null) {
               break;
            }

            var6 = false;
            Class[] var7 = this.toIgnoreAlert;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Class var10 = var7[var9];
               if (var5.getClass() == var10) {
                  var6 = true;
                  break;
               }
            }
         } while(var6);

         this.alertOther(var5, this.mob.getLastHurtByMob());
      }
   }

   protected void alertOther(Mob var1, LivingEntity var2) {
      var1.setTarget(var2);
   }
}
