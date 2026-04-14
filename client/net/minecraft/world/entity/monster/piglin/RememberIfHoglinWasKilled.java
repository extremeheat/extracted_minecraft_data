package net.minecraft.world.entity.monster.piglin;

import java.util.function.Function;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class RememberIfHoglinWasKilled {
   public RememberIfHoglinWasKilled() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.ATTACK_TARGET), i.registered(MemoryModuleType.HUNTED_RECENTLY)).apply(i, (attackTarget, huntedRecently) -> (level, body, timestamp) -> {
               LivingEntity target = (LivingEntity)i.get(attackTarget);
               if (target.is(EntityTypes.HOGLIN) && target.isDeadOrDying()) {
                  huntedRecently.setWithExpiry(true, (long)PiglinAi.TIME_BETWEEN_HUNTS.sample(body.level().getRandom()));
               }

               return true;
            })));
   }
}
