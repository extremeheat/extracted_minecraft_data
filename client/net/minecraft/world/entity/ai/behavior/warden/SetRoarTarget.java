package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetRoarTarget<E extends Warden> extends Behavior<E> {
   private final Function<E, Optional<? extends LivingEntity>> targetFinderFunction;

   public SetRoarTarget(Function<E, Optional<? extends LivingEntity>> var1) {
      super(
         ImmutableMap.of(
            MemoryModuleType.ROAR_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryStatus.REGISTERED
         )
      );
      this.targetFinderFunction = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.targetFinderFunction.apply((E)var2).filter(var2::canTargetEntity).isPresent();
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      this.targetFinderFunction.apply((E)var2).ifPresent(var1x -> {
         var2.getBrain().setMemory(MemoryModuleType.ROAR_TARGET, var1x);
         var2.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      });
   }
}
