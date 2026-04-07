package net.minecraft.world.entity.ai.behavior;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class MeleeAttack {
   public MeleeAttack() {
      super();
   }

   public static <T extends Mob> OneShot<T> create(final int cooldownBetweenAttacks) {
      return create((body) -> true, cooldownBetweenAttacks);
   }

   public static <T extends Mob> OneShot<T> create(final Predicate<T> canAttackPredicate, final int cooldownBetweenAttacks) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.LOOK_TARGET), i.present(MemoryModuleType.ATTACK_TARGET), i.absent(MemoryModuleType.ATTACK_COOLING_DOWN), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (lookTarget, attackTarget, attackCoolingDown, nearestEntities) -> (level, body, timestamp) -> {
               LivingEntity target = (LivingEntity)i.get(attackTarget);
               if (canAttackPredicate.test(body) && !isHoldingUsableNonMeleeWeapon(body) && body.isWithinMeleeAttackRange(target) && ((NearestVisibleLivingEntities)i.get(nearestEntities)).contains(target)) {
                  lookTarget.set(new EntityTracker(target, true));
                  body.swing(InteractionHand.MAIN_HAND);
                  body.doHurtTarget(level, target);
                  attackCoolingDown.setWithExpiry(true, (long)cooldownBetweenAttacks);
                  return true;
               } else {
                  return false;
               }
            })));
   }

   private static boolean isHoldingUsableNonMeleeWeapon(final Mob body) {
      Objects.requireNonNull(body);
      return body.isHolding(body::canUseNonMeleeWeapon);
   }
}
