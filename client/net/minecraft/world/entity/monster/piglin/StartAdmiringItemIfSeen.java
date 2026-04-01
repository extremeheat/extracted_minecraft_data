package net.minecraft.world.entity.monster.piglin;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.livingblock.LivingBlock;

public class StartAdmiringItemIfSeen {
   public StartAdmiringItemIfSeen() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(final int admireDuration) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), i.absent(MemoryModuleType.ADMIRING_ITEM), i.absent(MemoryModuleType.ADMIRING_DISABLED), i.absent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(i, (nearestItem, admiring, admiringDisabled, walkDisabled) -> (level, body, timestamp) -> {
               LivingBlock itemEntity = (LivingBlock)i.get(nearestItem);
               if (!PiglinAi.isLovedItem(itemEntity.getItemStack())) {
                  return false;
               } else {
                  admiring.setWithExpiry(true, (long)admireDuration);
                  return true;
               }
            })));
   }
}
