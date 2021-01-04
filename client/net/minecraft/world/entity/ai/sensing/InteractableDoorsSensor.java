package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.dimension.DimensionType;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
   public InteractableDoorsSensor() {
      super();
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      DimensionType var3 = var1.getDimension().getType();
      BlockPos var4 = new BlockPos(var2);
      ArrayList var5 = Lists.newArrayList();

      for(int var6 = -1; var6 <= 1; ++var6) {
         for(int var7 = -1; var7 <= 1; ++var7) {
            for(int var8 = -1; var8 <= 1; ++var8) {
               BlockPos var9 = var4.offset(var6, var7, var8);
               if (var1.getBlockState(var9).is(BlockTags.WOODEN_DOORS)) {
                  var5.add(GlobalPos.of(var3, var9));
               }
            }
         }
      }

      Brain var10 = var2.getBrain();
      if (!var5.isEmpty()) {
         var10.setMemory(MemoryModuleType.INTERACTABLE_DOORS, (Object)var5);
      } else {
         var10.eraseMemory(MemoryModuleType.INTERACTABLE_DOORS);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
   }
}
