package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class DismountOrSkipMounting {
   public DismountOrSkipMounting() {
      super();
   }

   public static <E extends LivingEntity> BehaviorControl<E> create(int var0, BiPredicate<E, Entity> var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.registered(MemoryModuleType.RIDE_TARGET)).apply(var2, (var3) -> {
            return (var4, var5, var6) -> {
               Entity var8 = var5.getVehicle();
               Entity var9 = (Entity)var2.tryGet(var3).orElse((Object)null);
               if (var8 == null && var9 == null) {
                  return false;
               } else {
                  Entity var10 = var8 == null ? var9 : var8;
                  if (isVehicleValid(var5, var10, var0) && !var1.test(var5, var10)) {
                     return false;
                  } else {
                     var5.stopRiding();
                     var3.erase();
                     return true;
                  }
               }
            };
         });
      });
   }

   private static boolean isVehicleValid(LivingEntity var0, Entity var1, int var2) {
      return var1.isAlive() && var1.closerThan(var0, (double)var2) && var1.level() == var0.level();
   }
}
