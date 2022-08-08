package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
   public NearestVisibleLivingEntitySensor() {
      super();
   }

   protected abstract boolean isMatchingEntity(LivingEntity var1, LivingEntity var2);

   protected abstract MemoryModuleType<LivingEntity> getMemory();

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(this.getMemory());
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().setMemory(this.getMemory(), this.getNearestEntity(var2));
   }

   private Optional<LivingEntity> getNearestEntity(LivingEntity var1) {
      return this.getVisibleEntities(var1).flatMap((var2) -> {
         return var2.findClosest((var2x) -> {
            return this.isMatchingEntity(var1, var2x);
         });
      });
   }

   protected Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity var1) {
      return var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
