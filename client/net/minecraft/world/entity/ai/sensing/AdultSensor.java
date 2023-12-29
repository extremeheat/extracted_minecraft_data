package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class AdultSensor extends Sensor<AgeableMob> {
   public AdultSensor() {
      super();
   }

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }

   protected void doTick(ServerLevel var1, AgeableMob var2) {
      var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(var2x -> this.setNearestVisibleAdult(var2, var2x));
   }

   private void setNearestVisibleAdult(AgeableMob var1, NearestVisibleLivingEntities var2) {
      Optional var3 = var2.findClosest(var1x -> var1x.getType() == var1.getType() && !var1x.isBaby()).map(AgeableMob.class::cast);
      var1.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, var3);
   }
}
