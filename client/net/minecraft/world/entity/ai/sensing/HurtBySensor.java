package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class HurtBySensor extends Sensor<LivingEntity> {
   public HurtBySensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      DamageSource var4 = var2.getLastDamageSource();
      if (var4 != null) {
         var3.setMemory(MemoryModuleType.HURT_BY, (Object)var2.getLastDamageSource());
         Entity var5 = var4.getEntity();
         if (var5 instanceof LivingEntity) {
            var3.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object)((LivingEntity)var5));
         }
      } else {
         var3.eraseMemory(MemoryModuleType.HURT_BY);
      }

      var3.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((var2x) -> {
         if (!var2x.isAlive() || var2x.level() != var1) {
            var3.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         }

      });
   }
}
