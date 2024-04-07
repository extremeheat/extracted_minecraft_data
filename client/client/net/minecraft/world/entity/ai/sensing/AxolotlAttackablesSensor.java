package net.minecraft.world.entity.ai.sensing;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {
   public static final float TARGET_DETECTION_DISTANCE = 8.0F;

   public AxolotlAttackablesSensor() {
      super();
   }

   @Override
   protected boolean isMatchingEntity(LivingEntity var1, LivingEntity var2) {
      return this.isClose(var1, var2)
         && var2.isInWaterOrBubble()
         && (this.isHostileTarget(var2) || this.isHuntTarget(var1, var2))
         && Sensor.isEntityAttackable(var1, var2);
   }

   private boolean isHuntTarget(LivingEntity var1, LivingEntity var2) {
      return !var1.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && var2.getType().is(EntityTypeTags.AXOLOTL_HUNT_TARGETS);
   }

   private boolean isHostileTarget(LivingEntity var1) {
      return var1.getType().is(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES);
   }

   private boolean isClose(LivingEntity var1, LivingEntity var2) {
      return var2.distanceToSqr(var1) <= 64.0;
   }

   @Override
   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_ATTACKABLE;
   }
}
