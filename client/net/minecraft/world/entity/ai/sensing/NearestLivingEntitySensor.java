package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.AABB;

public class NearestLivingEntitySensor<T extends LivingEntity> extends Sensor<T> {
   public NearestLivingEntitySensor() {
      super();
   }

   protected void doTick(ServerLevel var1, T var2) {
      AABB var3 = var2.getBoundingBox().inflate((double)this.radiusXZ(), (double)this.radiusY(), (double)this.radiusXZ());
      List var4 = var1.getEntitiesOfClass(LivingEntity.class, var3, (var1x) -> {
         return var1x != var2 && var1x.isAlive();
      });
      Objects.requireNonNull(var2);
      var4.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Brain var5 = var2.getBrain();
      var5.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, (Object)var4);
      var5.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, (Object)(new NearestVisibleLivingEntities(var2, var4)));
   }

   protected int radiusXZ() {
      return 16;
   }

   protected int radiusY() {
      return 16;
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
