package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.livingblock.LivingBlock;

public class StopAdmiringIfItemTooFarAway<E extends Piglin> {
   public StopAdmiringIfItemTooFarAway() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(final int maxDistanceToItem) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.ADMIRING_ITEM), i.registered(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(i, (admiring, nearest) -> (level, body, timestamp) -> {
               if (!body.getOffhandItem().isEmpty()) {
                  return false;
               } else {
                  Optional<LivingBlock> nearestVisibleWantedItem = i.<LivingBlock>tryGet(nearest);
                  if (nearestVisibleWantedItem.isPresent() && ((LivingBlock)nearestVisibleWantedItem.get()).closerThan(body, (double)maxDistanceToItem)) {
                     return false;
                  } else {
                     admiring.erase();
                     return true;
                  }
               }
            })));
   }
}
