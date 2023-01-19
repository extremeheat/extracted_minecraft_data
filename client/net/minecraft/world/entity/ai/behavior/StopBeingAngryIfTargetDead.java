package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead<E extends Mob> extends Behavior<E> {
   public StopBeingAngryIfTargetDead() {
      super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT));
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      BehaviorUtils.getLivingEntityFromUUIDMemory(var2, MemoryModuleType.ANGRY_AT).ifPresent(var2x -> {
         if (var2x.isDeadOrDying() && (var2x.getType() != EntityType.PLAYER || var1.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
            var2.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
         }
      });
   }
}
