package net.minecraft.world.entity.ai.sensing;

import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
   public NearestVisibleLivingEntitySensor() {
      super();
   }

   protected abstract boolean isMatchingEntity(final ServerLevel level, LivingEntity body, Entity mob);

   protected abstract MemoryModuleType<Entity> getMemoryToSet();

   public Set<MemoryModuleType<?>> requires() {
      return Set.of(this.getMemoryToSet(), MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(final ServerLevel level, final LivingEntity body) {
      body.getBrain().setMemory(this.getMemoryToSet(), this.getNearestEntity(level, body));
   }

   private Optional<Entity> getNearestEntity(final ServerLevel level, final LivingEntity body) {
      return this.getVisibleEntities(body).flatMap((livingEntities) -> livingEntities.findClosest((mob) -> this.isMatchingEntity(level, body, mob)));
   }

   protected Optional<NearestVisibleLivingEntities> getVisibleEntities(final LivingEntity body) {
      return body.getBrain().<NearestVisibleLivingEntities>getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
