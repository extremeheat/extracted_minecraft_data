package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor extends Sensor<LivingEntity> {
   public PiglinBruteSpecificSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      Optional var4 = Optional.empty();
      ArrayList var5 = Lists.newArrayList();
      List var6 = (List)var3.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of());
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         LivingEntity var8 = (LivingEntity)var7.next();
         if (var8 instanceof WitherSkeleton || var8 instanceof WitherBoss) {
            var4 = Optional.of((Mob)var8);
            break;
         }
      }

      List var10 = (List)var3.getMemory(MemoryModuleType.LIVING_ENTITIES).orElse(ImmutableList.of());
      Iterator var11 = var10.iterator();

      while(var11.hasNext()) {
         LivingEntity var9 = (LivingEntity)var11.next();
         if (var9 instanceof AbstractPiglin && ((AbstractPiglin)var9).isAdult()) {
            var5.add((AbstractPiglin)var9);
         }
      }

      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, var4);
      var3.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object)var5);
   }
}
