package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerHostilesSensor extends Sensor<LivingEntity> {
   private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;

   public VillagerHostilesSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(var2));
   }

   private Optional<LivingEntity> getNearestHostile(LivingEntity var1) {
      return this.getVisibleEntities(var1).flatMap((var2) -> {
         return var2.stream().filter(this::isHostile).filter((var2x) -> {
            return this.isClose(var1, var2x);
         }).min((var2x, var3) -> {
            return this.compareMobDistance(var1, var2x, var3);
         });
      });
   }

   private Optional<List<LivingEntity>> getVisibleEntities(LivingEntity var1) {
      return var1.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }

   private int compareMobDistance(LivingEntity var1, LivingEntity var2, LivingEntity var3) {
      return Mth.floor(var2.distanceToSqr(var1) - var3.distanceToSqr(var1));
   }

   private boolean isClose(LivingEntity var1, LivingEntity var2) {
      float var3 = (Float)ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(var2.getType());
      return var2.distanceToSqr(var1) <= (double)(var3 * var3);
   }

   private boolean isHostile(LivingEntity var1) {
      return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(var1.getType());
   }

   static {
      ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F).put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F).put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOGLIN, 10.0F).put(EntityType.ZOMBIE, 8.0F).put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();
   }
}
