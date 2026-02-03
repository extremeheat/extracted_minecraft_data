package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromLookTarget {
   public SetWalkTargetFromLookTarget() {
      super();
   }

   public static OneShot<LivingEntity> create(final float speedModifier, final int closeEnoughDistance) {
      return create((mob) -> true, (mob) -> speedModifier, closeEnoughDistance);
   }

   public static OneShot<LivingEntity> create(final Predicate<LivingEntity> canSetWalkTargetPredicate, final Function<LivingEntity, Float> speedModifier, final int closeEnoughDistance) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.WALK_TARGET), i.present(MemoryModuleType.LOOK_TARGET)).apply(i, (walkTarget, lookTarget) -> (level, body, timestamp) -> {
               if (!canSetWalkTargetPredicate.test(body)) {
                  return false;
               } else {
                  walkTarget.set(new WalkTarget((PositionTracker)i.get(lookTarget), (Float)speedModifier.apply(body), closeEnoughDistance));
                  return true;
               }
            })));
   }
}
