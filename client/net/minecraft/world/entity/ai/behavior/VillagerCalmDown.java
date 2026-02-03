package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerCalmDown {
   private static final int SAFE_DISTANCE_FROM_DANGER = 36;

   public VillagerCalmDown() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.HURT_BY), i.registered(MemoryModuleType.HURT_BY_ENTITY), i.registered(MemoryModuleType.NEAREST_HOSTILE)).apply(i, (hurtBy, hurtByEntity, nearestHostile) -> (level, body, timestamp) -> {
               boolean feelScared = i.tryGet(hurtBy).isPresent() || i.tryGet(nearestHostile).isPresent() || i.tryGet(hurtByEntity).filter((entity) -> entity.distanceToSqr(body) <= 36.0).isPresent();
               if (!feelScared) {
                  hurtBy.erase();
                  hurtByEntity.erase();
                  body.getBrain().updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
               }

               return true;
            })));
   }
}
