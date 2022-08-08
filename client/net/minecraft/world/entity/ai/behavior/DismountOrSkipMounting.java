package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class DismountOrSkipMounting<E extends LivingEntity, T extends Entity> extends Behavior<E> {
   private final int maxWalkDistToRideTarget;
   private final BiPredicate<E, Entity> dontRideIf;

   public DismountOrSkipMounting(int var1, BiPredicate<E, Entity> var2) {
      super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryStatus.REGISTERED));
      this.maxWalkDistToRideTarget = var1;
      this.dontRideIf = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      Entity var3 = var2.getVehicle();
      Entity var4 = (Entity)var2.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).orElse((Object)null);
      if (var3 == null && var4 == null) {
         return false;
      } else {
         Entity var5 = var3 == null ? var4 : var3;
         return !this.isVehicleValid(var2, var5) || this.dontRideIf.test(var2, var5);
      }
   }

   private boolean isVehicleValid(E var1, Entity var2) {
      return var2.isAlive() && var2.closerThan(var1, (double)this.maxWalkDistToRideTarget) && var2.level == var1.level;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.stopRiding();
      var2.getBrain().eraseMemory(MemoryModuleType.RIDE_TARGET);
   }
}
