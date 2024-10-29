package net.minecraft.world.entity.ai.sensing;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {
   public static final float TARGET_DETECTION_DISTANCE = 8.0F;

   public AxolotlAttackablesSensor() {
      super();
   }

   protected boolean isMatchingEntity(ServerLevel var1, LivingEntity var2, LivingEntity var3) {
      return this.isClose(var2, var3) && var3.isInWaterOrBubble() && (this.isHostileTarget(var3) || this.isHuntTarget(var2, var3)) && Sensor.isEntityAttackable(var1, var2, var3);
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

   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_ATTACKABLE;
   }
}
