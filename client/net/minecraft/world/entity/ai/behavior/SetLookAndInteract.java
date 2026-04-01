package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetLookAndInteract {
   public SetLookAndInteract() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(final EntityType<?> type, final int interactionRange) {
      int interactionRangeSqr = interactionRange * interactionRange;
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.LOOK_TARGET), i.absent(MemoryModuleType.INTERACTION_TARGET), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (lookTarget, interactionTarget, nearestEntities) -> (level, body, timestamp) -> {
               Optional<Entity> closest = ((NearestVisibleLivingEntities)i.get(nearestEntities)).findClosestMatchingLivingEntityPredicate((e) -> e.distanceToSqr(body) <= (double)interactionRangeSqr && e.is(type));
               if (closest.isEmpty()) {
                  return false;
               } else {
                  Entity closestEntity = (Entity)closest.get();
                  interactionTarget.set((LivingEntity)closestEntity);
                  lookTarget.set(new EntityTracker(closestEntity, true));
                  return true;
               }
            })));
   }
}
