package net.minecraft.world.entity.ai.sensing;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class MobSensor<T extends LivingEntity> extends Sensor<T> {
   private final BiPredicate<T, LivingEntity> mobTest;
   private final Predicate<T> readyTest;
   private final MemoryModuleType<Boolean> toSet;
   private final int memoryTimeToLive;

   public MobSensor(int var1, BiPredicate<T, LivingEntity> var2, Predicate<T> var3, MemoryModuleType<Boolean> var4, int var5) {
      super(var1);
      this.mobTest = var2;
      this.readyTest = var3;
      this.toSet = var4;
      this.memoryTimeToLive = var5;
   }

   protected void doTick(ServerLevel var1, T var2) {
      if (!this.readyTest.test(var2)) {
         this.clearMemory(var2);
      } else {
         this.checkForMobsNearby(var2);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return Set.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
   }

   public void checkForMobsNearby(T var1) {
      Optional var2 = var1.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
      if (!var2.isEmpty()) {
         boolean var3 = ((List)var2.get()).stream().anyMatch((var2x) -> {
            return this.mobTest.test(var1, var2x);
         });
         if (var3) {
            this.mobDetected(var1);
         }

      }
   }

   public void mobDetected(T var1) {
      var1.getBrain().setMemoryWithExpiry(this.toSet, true, (long)this.memoryTimeToLive);
   }

   public void clearMemory(T var1) {
      var1.getBrain().eraseMemory(this.toSet);
   }
}
