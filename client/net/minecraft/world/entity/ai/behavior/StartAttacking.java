package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StartAttacking<E extends Mob> extends Behavior<E> {
   private final Predicate<E> canAttackPredicate;
   private final Function<E, Optional<? extends LivingEntity>> targetFinderFunction;

   public StartAttacking(Predicate<E> var1, Function<E, Optional<? extends LivingEntity>> var2) {
      this(var1, var2, 60);
   }

   public StartAttacking(Predicate<E> var1, Function<E, Optional<? extends LivingEntity>> var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED), var3);
      this.canAttackPredicate = var1;
      this.targetFinderFunction = var2;
   }

   public StartAttacking(Function<E, Optional<? extends LivingEntity>> var1) {
      this((var0) -> {
         return true;
      }, var1);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      if (!this.canAttackPredicate.test(var2)) {
         return false;
      } else {
         Optional var3 = (Optional)this.targetFinderFunction.apply(var2);
         return var3.isPresent() ? var2.canAttack((LivingEntity)var3.get()) : false;
      }
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      ((Optional)this.targetFinderFunction.apply(var2)).ifPresent((var1x) -> {
         setAttackTarget(var2, var1x);
      });
   }

   public static <E extends Mob> void setAttackTarget(E var0, LivingEntity var1) {
      var0.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, (Object)var1);
      var0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }
}
