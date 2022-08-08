package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
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

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)this.getNearestVillagerBabies(var2));
   }

   private List<LivingEntity> getNearestVillagerBabies(LivingEntity var1) {
      return ImmutableList.copyOf(this.getVisibleEntities(var1).findAll(this::isVillagerBaby));
   }

   private boolean isVillagerBaby(LivingEntity var1) {
      return var1.getType() == EntityType.VILLAGER && var1.isBaby();
   }

   private NearestVisibleLivingEntities getVisibleEntities(LivingEntity var1) {
      return (NearestVisibleLivingEntities)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
   }
}
