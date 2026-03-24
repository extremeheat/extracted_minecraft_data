package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom {
   public SetWalkTargetAwayFrom() {
      super();
   }

   public static BehaviorControl<PathfinderMob> pos(final MemoryModuleType<BlockPos> memory, final float speedModifier, final int desiredDistance, final boolean interruptCurrentWalk) {
      return create(memory, speedModifier, desiredDistance, interruptCurrentWalk, Vec3::atBottomCenterOf);
   }

   public static OneShot<PathfinderMob> entity(final MemoryModuleType<? extends Entity> memory, final float speedModifier, final int desiredDistance, final boolean interruptCurrentWalk) {
      return create(memory, speedModifier, desiredDistance, interruptCurrentWalk, Entity::position);
   }

   private static <T> OneShot<PathfinderMob> create(final MemoryModuleType<T> walkAwayFromMemory, final float speedModifier, final int desiredDistance, final boolean interruptCurrentWalk, final Function<T, Vec3> toPosition) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.WALK_TARGET), i.present(walkAwayFromMemory)).apply(i, (walkTarget, walkAwayFrom) -> (level, body, timestamp) -> {
               Optional<WalkTarget> target = i.<WalkTarget>tryGet(walkTarget);
               if (target.isPresent() && !interruptCurrentWalk) {
                  return false;
               } else {
                  Vec3 bodyPosition = body.position();
                  Vec3 avoidPosition = (Vec3)toPosition.apply(i.get(walkAwayFrom));
                  if (!bodyPosition.closerThan(avoidPosition, (double)desiredDistance)) {
                     return false;
                  } else {
                     if (target.isPresent() && ((WalkTarget)target.get()).getSpeedModifier() == speedModifier) {
                        Vec3 currentDirection = ((WalkTarget)target.get()).getTarget().currentPosition().subtract(bodyPosition);
                        Vec3 avoidDirection = avoidPosition.subtract(bodyPosition);
                        if (currentDirection.dot(avoidDirection) < 0.0) {
                           return false;
                        }
                     }

                     for(int j = 0; j < 10; ++j) {
                        Vec3 fleeToPos = LandRandomPos.getPosAway(body, 16, 7, avoidPosition);
                        if (fleeToPos != null) {
                           walkTarget.set(new WalkTarget(fleeToPos, speedModifier, 0));
                           break;
                        }
                     }

                     return true;
                  }
               }
            })));
   }
}
