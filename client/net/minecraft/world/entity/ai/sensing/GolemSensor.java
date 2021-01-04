package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class GolemSensor extends Sensor<LivingEntity> {
   public GolemSensor() {
      this(200);
   }

   public GolemSensor(int var1) {
      super(var1);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      checkForNearbyGolem(var1.getGameTime(), var2);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES);
   }

   public static void checkForNearbyGolem(long var0, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      Optional var4 = var3.getMemory(MemoryModuleType.LIVING_ENTITIES);
      if (var4.isPresent()) {
         boolean var5 = ((List)var4.get()).stream().anyMatch((var0x) -> {
            return var0x.getType().equals(EntityType.IRON_GOLEM);
         });
         if (var5) {
            var3.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, (Object)var0);
         }

      }
   }
}
