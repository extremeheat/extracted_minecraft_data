package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.gamerules.GameRules;

public class StartCelebratingIfTargetDead {
   public StartCelebratingIfTargetDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(final int celebrateDuration, final BiPredicate<LivingEntity, Entity> dancePredicate) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.ATTACK_TARGET), i.registered(MemoryModuleType.ANGRY_AT), i.absent(MemoryModuleType.CELEBRATE_LOCATION), i.registered(MemoryModuleType.DANCING)).apply(i, (attackTarget, angryAt, celebrateAt, dancing) -> (level, body, timestamp) -> {
               Entity target = (Entity)i.get(attackTarget);
               if (target instanceof Targetable targetable) {
                  if (!targetable.isDeadOrDying()) {
                     return false;
                  }
               }

               if (dancePredicate.test(body, target)) {
                  dancing.setWithExpiry(true, (long)celebrateDuration);
               }

               celebrateAt.setWithExpiry(target.blockPosition(), (long)celebrateDuration);
               if (!target.is(EntityType.PLAYER) || (Boolean)level.getGameRules().get(GameRules.FORGIVE_DEAD_PLAYERS)) {
                  attackTarget.erase();
                  angryAt.erase();
               }

               return true;
            })));
   }
}
