package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class LookAtTargetSink extends Behavior<Mob> {
   public LookAtTargetSink(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), var1, var2);
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      return var2.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((var1x) -> {
         return var1x.isVisibleBy(var2);
      }).isPresent();
   }

   protected void stop(ServerLevel var1, Mob var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      var2.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((var1x) -> {
         var2.getLookControl().setLookAt(var1x.currentPosition());
      });
   }
}
