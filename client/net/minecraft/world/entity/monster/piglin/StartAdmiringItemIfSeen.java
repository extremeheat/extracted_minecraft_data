package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;

public class StartAdmiringItemIfSeen<E extends Piglin> extends Behavior<E> {
   private final int admireDuration;

   public StartAdmiringItemIfSeen(int var1) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ADMIRING_DISABLED, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.VALUE_ABSENT));
      this.admireDuration = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      ItemEntity var3 = (ItemEntity)var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
      return PiglinAi.isLovedItem(var3.getItem());
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, (long)this.admireDuration);
   }
}
