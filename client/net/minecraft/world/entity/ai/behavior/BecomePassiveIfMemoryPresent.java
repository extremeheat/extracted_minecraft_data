package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BecomePassiveIfMemoryPresent extends Behavior<LivingEntity> {
   private final int pacifyDuration;

   public BecomePassiveIfMemoryPresent(MemoryModuleType<?> var1, int var2) {
      super(
         ImmutableMap.of(
            MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.PACIFIED, MemoryStatus.VALUE_ABSENT, var1, MemoryStatus.VALUE_PRESENT
         )
      );
      this.pacifyDuration = var2;
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.PACIFIED, true, (long)this.pacifyDuration);
      var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
   }
}
