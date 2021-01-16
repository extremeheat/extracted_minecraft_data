package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StopAdmiringIfTiredOfTryingToReachItem<E extends Piglin> extends Behavior<E> {
   private final int maxTimeToReachItem;
   private final int disableTime;

   public StopAdmiringIfTiredOfTryingToReachItem(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryStatus.REGISTERED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.REGISTERED));
      this.maxTimeToReachItem = var1;
      this.disableTime = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return var2.getOffhandItem().isEmpty();
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      Optional var6 = var5.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
      if (!var6.isPresent()) {
         var5.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (int)0);
      } else {
         int var7 = (Integer)var6.get();
         if (var7 > this.maxTimeToReachItem) {
            var5.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
            var5.eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            var5.setMemoryWithExpiry(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, (long)this.disableTime);
         } else {
            var5.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (Object)(var7 + 1));
         }
      }

   }
}
