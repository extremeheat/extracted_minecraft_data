package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
   public StartAttacking() {
      super();
   }

   public static <E extends Mob> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> var0) {
      return create((var0x) -> {
         return true;
      }, var0);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<E> var0, Function<E, Optional<? extends LivingEntity>> var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.absent(MemoryModuleType.ATTACK_TARGET), var2.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(var2, (var2x, var3) -> {
            return (var4, var5, var6) -> {
               if (!var0.test(var5)) {
                  return false;
               } else {
                  Optional var8 = (Optional)var1.apply(var5);
                  if (var8.isEmpty()) {
                     return false;
                  } else {
                     LivingEntity var9 = (LivingEntity)var8.get();
                     if (!var5.canAttack(var9)) {
                        return false;
                     } else {
                        var2x.set(var9);
                        var3.erase();
                        return true;
                     }
                  }
               }
            };
         });
      });
   }
}
