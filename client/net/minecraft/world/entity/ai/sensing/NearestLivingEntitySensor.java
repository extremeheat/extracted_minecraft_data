package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.AABB;

public class NearestLivingEntitySensor<T extends LivingEntity> extends Sensor<T> {
   public NearestLivingEntitySensor() {
      super();
   }

   protected void doTick(ServerLevel var1, T var2) {
      double var3 = var2.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB var5 = var2.getBoundingBox().inflate(var3, var3, var3);
      List var6 = var1.getEntitiesOfClass(LivingEntity.class, var5, (var1x) -> var1x != var2 && var1x.isAlive());
      Objects.requireNonNull(var2);
      var6.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Brain var7 = var2.getBrain();
      var7.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, var6);
      var7.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(var1, var2, var6));
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
