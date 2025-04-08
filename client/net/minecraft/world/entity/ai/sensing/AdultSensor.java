package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class AdultSensor extends Sensor<LivingEntity> {
   public AdultSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((var2x) -> this.setNearestVisibleAdult(var2, var2x));
   }

   protected void setNearestVisibleAdult(LivingEntity var1, NearestVisibleLivingEntities var2) {
      Optional var10000 = var2.findClosest((var1x) -> var1x.getType() == var1.getType() && !var1x.isBaby());
      Objects.requireNonNull(LivingEntity.class);
      Optional var3 = var10000.map(LivingEntity.class::cast);
      var1.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, var3);
   }
}
