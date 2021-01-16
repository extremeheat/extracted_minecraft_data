package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RememberIfHoglinWasKilled<E extends Piglin> extends Behavior<E> {
   public RememberIfHoglinWasKilled() {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.REGISTERED));
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      if (this.isAttackTargetDeadHoglin(var2)) {
         PiglinAi.dontKillAnyMoreHoglinsForAWhile(var2);
      }

   }

   private boolean isAttackTargetDeadHoglin(E var1) {
      LivingEntity var2 = (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      return var2.getType() == EntityType.HOGLIN && var2.isDeadOrDying();
   }
}
