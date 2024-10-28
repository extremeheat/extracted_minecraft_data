package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class SecondaryPoiSensor extends Sensor<Villager> {
   private static final int SCAN_RATE = 40;

   public SecondaryPoiSensor() {
      super(40);
   }

   protected void doTick(ServerLevel var1, Villager var2) {
      ResourceKey var3 = var1.dimension();
      BlockPos var4 = var2.blockPosition();
      ArrayList var5 = Lists.newArrayList();
      boolean var6 = true;

      for(int var7 = -4; var7 <= 4; ++var7) {
         for(int var8 = -2; var8 <= 2; ++var8) {
            for(int var9 = -4; var9 <= 4; ++var9) {
               BlockPos var10 = var4.offset(var7, var8, var9);
               if (var2.getVillagerData().getProfession().secondaryPoi().contains(var1.getBlockState(var10).getBlock())) {
                  var5.add(GlobalPos.of(var3, var10));
               }
            }
         }
      }

      Brain var11 = var2.getBrain();
      if (!var5.isEmpty()) {
         var11.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, (Object)var5);
      } else {
         var11.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}
