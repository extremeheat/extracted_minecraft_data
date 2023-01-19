package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
   public PiglinBruteSpecificSensor() {
      super();
   }

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
   }

   @Override
   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      ArrayList var4 = Lists.newArrayList();
      NearestVisibleLivingEntities var5 = var3.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
      Optional var6 = var5.findClosest(var0 -> var0 instanceof WitherSkeleton || var0 instanceof WitherBoss).map(Mob.class::cast);

      for(LivingEntity var9 : var3.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
         if (var9 instanceof AbstractPiglin && ((AbstractPiglin)var9).isAdult()) {
            var4.add((AbstractPiglin)var9);
         }
      }

      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, var6);
      var3.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, var4);
   }
}
