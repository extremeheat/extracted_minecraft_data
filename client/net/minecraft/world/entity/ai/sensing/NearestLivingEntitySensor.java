package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AABB;

public class NearestLivingEntitySensor extends Sensor<LivingEntity> {
   public NearestLivingEntitySensor() {
      super();
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      AABB var3 = var2.getBoundingBox().inflate(16.0D, 16.0D, 16.0D);
      List var4 = var1.getEntitiesOfClass(LivingEntity.class, var3, (var1x) -> {
         return var1x != var2 && var1x.isAlive();
      });
      var2.getClass();
      var4.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Brain var5 = var2.getBrain();
      var5.setMemory(MemoryModuleType.LIVING_ENTITIES, (Object)var4);
      var5.setMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES, var4.stream().filter((var1x) -> {
         return isEntityTargetable(var2, var1x);
      }).collect(Collectors.toList()));
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }
}
