package net.minecraft.world.entity.ai.behavior.warden;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetRoarTarget {
   public SetRoarTarget() {
      super();
   }

   public static <E extends Warden> BehaviorControl<E> create(final Function<E, Optional<? extends LivingEntity>> targetFinderFunction) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.ROAR_TARGET), i.absent(MemoryModuleType.ATTACK_TARGET), i.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(i, (roarTarget, attackTarget, cantReachSince) -> (level, body, timestamp) -> {
               Optional<? extends LivingEntity> target = (Optional)targetFinderFunction.apply(body);
               Objects.requireNonNull(body);
               if (target.filter(body::canTargetEntity).isEmpty()) {
                  return false;
               } else {
                  roarTarget.set((LivingEntity)target.get());
                  cantReachSince.erase();
                  return true;
               }
            })));
   }
}
