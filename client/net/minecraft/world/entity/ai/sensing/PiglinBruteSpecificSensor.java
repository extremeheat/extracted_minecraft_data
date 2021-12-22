package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      ArrayList var4 = Lists.newArrayList();
      NearestVisibleLivingEntities var5 = (NearestVisibleLivingEntities)var3.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
      Optional var10000 = var5.findClosest((var0) -> {
         return var0 instanceof WitherSkeleton || var0 instanceof WitherBoss;
      });
      Objects.requireNonNull(Mob.class);
      Optional var6 = var10000.map(Mob.class::cast);
      List var7 = (List)var3.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of());
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         LivingEntity var9 = (LivingEntity)var8.next();
         if (var9 instanceof AbstractPiglin && ((AbstractPiglin)var9).isAdult()) {
            var4.add((AbstractPiglin)var9);
         }
      }

      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, var6);
      var3.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object)var4);
   }
}
