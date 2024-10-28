package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class IsInWaterSensor extends Sensor<LivingEntity> {
   public IsInWaterSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.IS_IN_WATER);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      if (var2.isInWater()) {
         var2.getBrain().setMemory(MemoryModuleType.IS_IN_WATER, (Object)Unit.INSTANCE);
      } else {
         var2.getBrain().eraseMemory(MemoryModuleType.IS_IN_WATER);
      }

   }
}
