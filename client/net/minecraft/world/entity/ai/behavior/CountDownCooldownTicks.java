package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CountDownCooldownTicks extends Behavior<LivingEntity> {
   private final MemoryModuleType<Integer> cooldownTicks;

   public CountDownCooldownTicks(MemoryModuleType<Integer> var1) {
      super(ImmutableMap.of(var1, MemoryStatus.VALUE_PRESENT));
      this.cooldownTicks = var1;
   }

   private Optional<Integer> getCooldownTickMemory(LivingEntity var1) {
      return var1.getBrain().getMemory(this.cooldownTicks);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      Optional var5 = this.getCooldownTickMemory(var2);
      return var5.isPresent() && (Integer)var5.get() > 0;
   }

   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      Optional var5 = this.getCooldownTickMemory(var2);
      var2.getBrain().setMemory(this.cooldownTicks, (Object)((Integer)var5.get() - 1));
   }

   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      var2.getBrain().eraseMemory(this.cooldownTicks);
   }
}
