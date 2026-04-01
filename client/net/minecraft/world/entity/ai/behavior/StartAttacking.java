package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
   public StartAttacking() {
      super();
   }

   public static <E extends Mob> BehaviorControl<E> create(final TargetFinder<E> targetFinderFunction) {
      return create((level, body) -> true, targetFinderFunction);
   }

   public static <E extends Mob> BehaviorControl<E> create(final StartAttackingCondition<E> canAttackPredicate, final TargetFinder<E> targetFinderFunction) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.ATTACK_TARGET), i.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(i, (attackTarget, cantReachSince) -> (level, body, timestamp) -> {
               if (!canAttackPredicate.test(level, body)) {
                  return false;
               } else {
                  Optional<? extends Entity> target = targetFinderFunction.get(level, body);
                  if (target.isEmpty()) {
                     return false;
                  } else {
                     Entity targetEntity = (Entity)target.get();
                     if (!body.canAttack(targetEntity)) {
                        return false;
                     } else {
                        attackTarget.set(targetEntity);
                        cantReachSince.erase();
                        return true;
                     }
                  }
               }
            })));
   }

   @FunctionalInterface
   public interface StartAttackingCondition<E> {
      boolean test(ServerLevel level, E body);
   }

   @FunctionalInterface
   public interface TargetFinder<E> {
      Optional<? extends Entity> get(ServerLevel level, E body);
   }
}
