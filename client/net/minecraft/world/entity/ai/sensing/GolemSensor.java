package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class GolemSensor extends Sensor<LivingEntity> {
   private static final int GOLEM_SCAN_RATE = 200;
   private static final int MEMORY_TIME_TO_LIVE = 599;

   public GolemSensor() {
      this(200);
   }

   public GolemSensor(int var1) {
      super(var1);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      checkForNearbyGolem(var2);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
   }

   public static void checkForNearbyGolem(LivingEntity var0) {
      Optional var1 = var0.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
      if (!var1.isEmpty()) {
         boolean var2 = ((List)var1.get()).stream().anyMatch((var0x) -> {
            return var0x.getType().equals(EntityType.IRON_GOLEM);
         });
         if (var2) {
            golemDetected(var0);
         }

      }
   }

   public static void golemDetected(LivingEntity var0) {
      var0.getBrain().setMemoryWithExpiry(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 599L);
   }
}
