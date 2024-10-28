package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopAttackingIfTargetInvalid {
   private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;

   public StopAttackingIfTargetInvalid() {
      super();
   }

   public static <E extends Mob> BehaviorControl<E> create(BiConsumer<E, LivingEntity> var0) {
      return create((var0x) -> {
         return false;
      }, var0, true);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> var0) {
      return create(var0, (var0x, var1) -> {
      }, true);
   }

   public static <E extends Mob> BehaviorControl<E> create() {
      return create((var0) -> {
         return false;
      }, (var0, var1) -> {
      }, true);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> var0, BiConsumer<E, LivingEntity> var1, boolean var2) {
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.present(MemoryModuleType.ATTACK_TARGET), var3.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(var3, (var4, var5) -> {
            return (var6, var7, var8) -> {
               LivingEntity var10 = (LivingEntity)var3.get(var4);
               if (var7.canAttack(var10) && (!var2 || !isTiredOfTryingToReachTarget(var7, var3.tryGet(var5))) && var10.isAlive() && var10.level() == var7.level() && !var0.test(var10)) {
                  return true;
               } else {
                  var1.accept(var7, var10);
                  var4.erase();
                  return true;
               }
            };
         });
      });
   }

   private static boolean isTiredOfTryingToReachTarget(LivingEntity var0, Optional<Long> var1) {
      return var1.isPresent() && var0.level().getGameTime() - (Long)var1.get() > 200L;
   }
}
