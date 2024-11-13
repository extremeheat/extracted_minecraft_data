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

   protected abstract boolean isMatchingEntity(ServerLevel var1, LivingEntity var2, LivingEntity var3);

   protected abstract MemoryModuleType<LivingEntity> getMemory();

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(this.getMemory());
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().setMemory(this.getMemory(), this.getNearestEntity(var1, var2));
   }

   private Optional<LivingEntity> getNearestEntity(ServerLevel var1, LivingEntity var2) {
      return this.getVisibleEntities(var2).flatMap((var3) -> var3.findClosest((var3x) -> this.isMatchingEntity(var1, var2, var3x)));
   }

   protected Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity var1) {
      return var1.getBrain().<NearestVisibleLivingEntities>getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
