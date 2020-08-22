package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerBabiesSensor extends Sensor {
   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      var2.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)this.getNearestVillagerBabies(var2));
   }

   private List getNearestVillagerBabies(LivingEntity var1) {
      return (List)this.getVisibleEntities(var1).stream().filter(this::isVillagerBaby).collect(Collectors.toList());
   }

   private boolean isVillagerBaby(LivingEntity var1) {
      return var1.getType() == EntityType.VILLAGER && var1.isBaby();
   }

   private List getVisibleEntities(LivingEntity var1) {
      return (List)var1.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(Lists.newArrayList());
   }
}
