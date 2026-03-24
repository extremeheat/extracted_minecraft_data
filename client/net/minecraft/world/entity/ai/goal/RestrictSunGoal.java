package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;

public class RestrictSunGoal extends Goal {
   private final PathfinderMob mob;

   public RestrictSunGoal(final PathfinderMob mob) {
      super();
      this.mob = mob;
   }

   public boolean canUse() {
      return this.mob.level().isBrightOutside() && this.mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && GoalUtils.hasGroundPathNavigation(this.mob);
   }

   public void start() {
      PathNavigation var2 = this.mob.getNavigation();
      if (var2 instanceof GroundPathNavigation pathNavigation) {
         pathNavigation.setAvoidSun(true);
      }

   }

   public void stop() {
      if (GoalUtils.hasGroundPathNavigation(this.mob)) {
         PathNavigation var2 = this.mob.getNavigation();
         if (var2 instanceof GroundPathNavigation) {
            GroundPathNavigation pathNavigation = (GroundPathNavigation)var2;
            pathNavigation.setAvoidSun(false);
         }
      }

   }
}
