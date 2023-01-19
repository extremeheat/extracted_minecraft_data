package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class EraseMemoryIf {
   public EraseMemoryIf() {
      super();
   }

   public static <E extends LivingEntity> BehaviorControl<E> create(Predicate<E> var0, MemoryModuleType<?> var1) {
      return BehaviorBuilder.create(var2 -> var2.group(var2.present(var1)).apply(var2, var1xx -> (var2x, var3, var4) -> {
               if (var0.test(var3)) {
                  var1xx.erase();
                  return true;
               } else {
                  return false;
               }
            }));
   }
}
