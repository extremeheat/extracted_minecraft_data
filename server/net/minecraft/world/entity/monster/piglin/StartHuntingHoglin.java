package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class StartHuntingHoglin<E extends Piglin> extends Behavior<E> {
   public StartHuntingHoglin() {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Piglin var2) {
      return !var2.isBaby() && !PiglinAi.hasAnyoneNearbyHuntedRecently(var2);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      Hoglin var5 = (Hoglin)var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
      PiglinAi.setAngerTarget(var2, var5);
      PiglinAi.dontKillAnyMoreHoglinsForAWhile(var2);
      PiglinAi.broadcastAngerTarget(var2, var5);
      PiglinAi.broadcastDontKillAnyMoreHoglinsForAWhile(var2);
   }
}
