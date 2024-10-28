package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeAttackEntitySensor extends NearestLivingEntitySensor<Breeze> {
   public static final int BREEZE_SENSOR_RADIUS = 24;

   public BreezeAttackEntitySensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
   }

   protected void doTick(ServerLevel var1, Breeze var2) {
      super.doTick(var1, var2);
      var2.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream).filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR).filter((var1x) -> {
         return Sensor.isEntityAttackable(var2, var1x);
      }).findFirst().ifPresentOrElse((var1x) -> {
         var2.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, (Object)var1x);
      }, () -> {
         var2.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
      });
   }

   protected int radiusXZ() {
      return 24;
   }

   protected int radiusY() {
      return 24;
   }
}
