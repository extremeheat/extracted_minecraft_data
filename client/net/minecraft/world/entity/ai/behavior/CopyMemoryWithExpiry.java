package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class CopyMemoryWithExpiry {
   public CopyMemoryWithExpiry() {
      super();
   }

   public static <E extends LivingEntity, T> BehaviorControl<E> create(Predicate<E> var0, MemoryModuleType<? extends T> var1, MemoryModuleType<T> var2, UniformInt var3) {
      return BehaviorBuilder.create((var4) -> {
         return var4.group(var4.present(var1), var4.absent(var2)).apply(var4, (var3x, var4x) -> {
            return (var5, var6, var7) -> {
               if (!var0.test(var6)) {
                  return false;
               } else {
                  var4x.setWithExpiry(var4.get(var3x), (long)var3.sample(var5.random));
                  return true;
               }
            };
         });
      });
   }
}
