package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Items;

public class StopHoldingItemIfNoLongerAdmiring {
   public StopHoldingItemIfNoLongerAdmiring() {
      super();
   }

   public static BehaviorControl<Piglin> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.absent(MemoryModuleType.ADMIRING_ITEM)).apply(var0, (var0x) -> {
            return (var0, var1, var2) -> {
               if (!var1.getOffhandItem().isEmpty() && !var1.getOffhandItem().is(Items.SHIELD)) {
                  PiglinAi.stopHoldingOffHandItem(var1, true);
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
