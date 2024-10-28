package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerHostilesSensor extends NearestVisibleLivingEntitySensor {
   private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;

   public VillagerHostilesSensor() {
      super();
   }

   protected boolean isMatchingEntity(LivingEntity var1, LivingEntity var2) {
      return this.isHostile(var2) && this.isClose(var1, var2);
   }

   private boolean isClose(LivingEntity var1, LivingEntity var2) {
      float var3 = (Float)ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(var2.getType());
      return var2.distanceToSqr(var1) <= (double)(var3 * var3);
   }

   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_HOSTILE;
   }

   private boolean isHostile(LivingEntity var1) {
      return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(var1.getType());
   }

   static {
      ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F).put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F).put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOGLIN, 10.0F).put(EntityType.ZOMBIE, 8.0F).put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();
   }
}
