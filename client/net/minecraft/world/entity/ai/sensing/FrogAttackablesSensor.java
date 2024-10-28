package net.minecraft.world.entity.ai.sensing;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogAttackablesSensor extends NearestVisibleLivingEntitySensor {
   public static final float TARGET_DETECTION_DISTANCE = 10.0F;

   public FrogAttackablesSensor() {
      super();
   }

   protected boolean isMatchingEntity(LivingEntity var1, LivingEntity var2) {
      return !var1.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && Sensor.isEntityAttackable(var1, var2) && Frog.canEat(var2) && !this.isUnreachableAttackTarget(var1, var2) ? var2.closerThan(var1, 10.0) : false;
   }

   private boolean isUnreachableAttackTarget(LivingEntity var1, LivingEntity var2) {
      List var3 = (List)var1.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
      return var3.contains(var2.getUUID());
   }

   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_ATTACKABLE;
   }
}
