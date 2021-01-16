package net.minecraft.world.entity.ai.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

public class GoalUtils {
   public static boolean hasGroundPathNavigation(Mob var0) {
      return var0.getNavigation() instanceof GroundPathNavigation;
   }
}
