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

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      if (var2.getLastDamageSource() != null) {
         var3.setMemory(MemoryModuleType.HURT_BY, (Object)var2.getLastDamageSource());
         Entity var4 = ((DamageSource)var3.getMemory(MemoryModuleType.HURT_BY).get()).getEntity();
         if (var4 instanceof LivingEntity) {
            var3.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object)((LivingEntity)var4));
         }
      } else {
         var3.eraseMemory(MemoryModuleType.HURT_BY);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }
}
