package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetWardenLookTarget extends Behavior<Warden> {
   public SetWardenLookTarget() {
      super(
         ImmutableMap.of(
            MemoryModuleType.DISTURBANCE_LOCATION,
            MemoryStatus.REGISTERED,
            MemoryModuleType.ROAR_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_ABSENT
         )
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Warden var2) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION) || var2.getBrain().hasMemoryValue(MemoryModuleType.ROAR_TARGET);
   }

   protected void start(ServerLevel var1, Warden var2, long var3) {
      BlockPos var5 = var2.getBrain()
         .getMemory(MemoryModuleType.ROAR_TARGET)
         .map(Entity::blockPosition)
         .or(() -> var2.getBrain().getMemory(MemoryModuleType.DISTURBANCE_LOCATION))
         .get();
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(var5));
   }
}
