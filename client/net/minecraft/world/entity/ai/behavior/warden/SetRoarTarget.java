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

   public static <E extends Warden> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.absent(MemoryModuleType.ROAR_TARGET), var1.absent(MemoryModuleType.ATTACK_TARGET), var1.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(var1, (var1x, var2, var3) -> {
            return (var3x, var4, var5) -> {
               Optional var7 = (Optional)var0.apply(var4);
               Objects.requireNonNull(var4);
               if (var7.filter(var4::canTargetEntity).isEmpty()) {
                  return false;
               } else {
                  var1x.set((LivingEntity)var7.get());
                  var3.erase();
                  return true;
               }
            };
         });
      });
   }
}
