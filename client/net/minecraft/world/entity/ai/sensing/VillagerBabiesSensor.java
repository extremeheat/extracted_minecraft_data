package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class VillagerBabiesSensor extends Sensor<LivingEntity> {
   public VillagerBabiesSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }

   protected void doTick(final ServerLevel level, final LivingEntity body) {
      body.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getNearestVillagerBabies(body));
   }

   private List<LivingEntity> getNearestVillagerBabies(final LivingEntity myBody) {
      return ImmutableList.copyOf(this.getVisibleEntities(myBody).findAll(this::isVillagerBaby)).stream().map((entity) -> (LivingEntity)entity).toList();
   }

   private boolean isVillagerBaby(final Entity entity) {
      if (!(entity instanceof LivingEntity livingEntity)) {
         return false;
      } else {
         return livingEntity.is(EntityType.VILLAGER) && livingEntity.isBaby();
      }
   }

   private NearestVisibleLivingEntities getVisibleEntities(final LivingEntity myBody) {
      return (NearestVisibleLivingEntities)myBody.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
   }
}
