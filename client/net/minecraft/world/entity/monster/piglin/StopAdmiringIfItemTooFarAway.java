package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class StopAdmiringIfItemTooFarAway<E extends Piglin> {
   public StopAdmiringIfItemTooFarAway() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(int var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.present(MemoryModuleType.ADMIRING_ITEM), var1.registered(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(var1, (var2, var3) -> {
            return (var4, var5, var6) -> {
               if (!var5.getOffhandItem().isEmpty()) {
                  return false;
               } else {
                  Optional var8 = var1.tryGet(var3);
                  if (var8.isPresent() && ((ItemEntity)var8.get()).closerThan(var5, (double)var0)) {
                     return false;
                  } else {
                     var2.erase();
                     return true;
                  }
               }
            };
         });
      });
   }
}
