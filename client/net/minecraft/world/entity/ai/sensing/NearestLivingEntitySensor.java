package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class NearestLivingEntitySensor extends Sensor<LivingEntity> {
   private static final TargetingConditions TARGETING = (new TargetingConditions()).range(16.0D).allowSameTeam().allowNonAttackable().allowUnseeable();

   public NearestLivingEntitySensor() {
      super();
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      List var3 = var1.getEntitiesOfClass(LivingEntity.class, var2.getBoundingBox().inflate(16.0D, 16.0D, 16.0D), (var1x) -> {
         return var1x != var2 && var1x.isAlive();
      });
      var2.getClass();
      var3.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Brain var4 = var2.getBrain();
      var4.setMemory(MemoryModuleType.LIVING_ENTITIES, (Object)var3);
      MemoryModuleType var10001 = MemoryModuleType.VISIBLE_LIVING_ENTITIES;
      Stream var10002 = var3.stream().filter((var1x) -> {
         return TARGETING.test(var2, var1x);
      });
      var2.getClass();
      var4.setMemory(var10001, var10002.filter(var2::canSee).collect(Collectors.toList()));
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }
}
