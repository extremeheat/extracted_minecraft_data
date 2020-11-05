package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AdultSensor extends Sensor<AgableMob> {
   public AdultSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerLevel var1, AgableMob var2) {
      var2.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((var2x) -> {
         this.setNearestVisibleAdult(var2, var2x);
      });
   }

   private void setNearestVisibleAdult(AgableMob var1, List<LivingEntity> var2) {
      Optional var3 = var2.stream().filter((var1x) -> {
         return var1x.getType() == var1.getType();
      }).map((var0) -> {
         return (AgableMob)var0;
      }).filter((var0) -> {
         return !var0.isBaby();
      }).findFirst();
      var1.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, var3);
   }
}
