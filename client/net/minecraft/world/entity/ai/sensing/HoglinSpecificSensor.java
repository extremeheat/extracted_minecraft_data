package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;

public class HoglinSpecificSensor extends Sensor<Hoglin> {
   public HoglinSpecificSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, new MemoryModuleType[0]);
   }

   protected void doTick(ServerLevel var1, Hoglin var2) {
      Brain var3 = var2.getBrain();
      var3.setMemory(MemoryModuleType.NEAREST_REPELLENT, this.findNearestRepellent(var1, var2));
      Optional var4 = Optional.empty();
      int var5 = 0;
      ArrayList var6 = Lists.newArrayList();
      NearestVisibleLivingEntities var7 = (NearestVisibleLivingEntities)var3.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());

      for(LivingEntity var9 : var7.findAll((var0) -> !var0.isBaby() && (var0 instanceof Piglin || var0 instanceof Hoglin))) {
         if (var9 instanceof Piglin var10) {
            ++var5;
            if (var4.isEmpty()) {
               var4 = Optional.of(var10);
            }
         }

         if (var9 instanceof Hoglin var11) {
            var6.add(var11);
         }
      }

      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, var4);
      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, var6);
      var3.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, var5);
      var3.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, var6.size());
   }

   private Optional<BlockPos> findNearestRepellent(ServerLevel var1, Hoglin var2) {
      return BlockPos.findClosestMatch(var2.blockPosition(), 8, 4, (var1x) -> var1.getBlockState(var1x).is(BlockTags.HOGLIN_REPELLENTS));
   }
}
