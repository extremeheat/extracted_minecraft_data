package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BackUpIfTooClose {
   public BackUpIfTooClose() {
      super();
   }

   public static OneShot<Mob> create(final int tooCloseDistance, final float strafeSpeed) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.WALK_TARGET), i.registered(MemoryModuleType.LOOK_TARGET), i.present(MemoryModuleType.ATTACK_TARGET), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (walkTarget, lookTarget, attackTarget, nearestVisible) -> (level, body, timestamp) -> {
               Entity target = (Entity)i.get(attackTarget);
               if (target.closerThan(body, (double)tooCloseDistance) && ((NearestVisibleLivingEntities)i.get(nearestVisible)).contains(target)) {
                  lookTarget.set(new EntityTracker(target, true));
                  body.getMoveControl().strafe(-strafeSpeed, 0.0F);
                  body.setYRot(Mth.rotateIfNecessary(body.getYRot(), body.yHeadRot, 0.0F));
                  return true;
               } else {
                  return false;
               }
            })));
   }
}
