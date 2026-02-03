package net.minecraft.world.entity.monster.piglin;

import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopHoldingItemIfNoLongerAdmiring {
   public StopHoldingItemIfNoLongerAdmiring() {
      super();
   }

   public static BehaviorControl<Piglin> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.ADMIRING_ITEM)).apply(i, (admiring) -> (level, body, timestamp) -> {
               if (!body.getOffhandItem().isEmpty() && !body.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS)) {
                  PiglinAi.stopHoldingOffHandItem(level, body, true);
                  return true;
               } else {
                  return false;
               }
            })));
   }
}
