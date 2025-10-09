package net.minecraft.world.entity.ai.sensing;

import java.util.Optional;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class AdultSensorAnyType extends AdultSensor {
   public AdultSensorAnyType() {
      super();
   }

   protected void setNearestVisibleAdult(LivingEntity var1, NearestVisibleLivingEntities var2) {
      Optional var3 = var2.findClosest((var0) -> var0.getType().is(EntityTypeTags.FOLLOWABLE_FRIENDLY_MOBS) && !var0.isBaby());
      var1.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, var3);
   }
}
