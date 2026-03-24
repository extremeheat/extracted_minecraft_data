package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class EraseMemoryIf {
   public EraseMemoryIf() {
      super();
   }

   public static <E extends LivingEntity> BehaviorControl<E> create(final Predicate<E> predicate, final MemoryModuleType<?> memoryType) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(memoryType)).apply(i, (memory) -> (level, body, timestamp) -> {
               if (predicate.test(body)) {
                  memory.erase();
                  return true;
               } else {
                  return false;
               }
            })));
   }
}
