package net.minecraft.world.entity.ai.targeting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Targetable;
import org.jspecify.annotations.Nullable;

public class LivingBlockTargetingConditions extends TargetingConditions {
   public LivingBlockTargetingConditions() {
      super(false);
   }

   public <T extends Entity & Targetable> boolean test(final ServerLevel level, final @Nullable Entity targeter, final Entity target) {
      return this.selector == null || this.selector.test(target, level);
   }
}
