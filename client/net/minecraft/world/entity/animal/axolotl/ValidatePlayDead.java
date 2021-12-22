package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class ValidatePlayDead extends Behavior<Axolotl> {
   public ValidatePlayDead() {
      super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT));
   }

   protected void start(ServerLevel var1, Axolotl var2, long var3) {
      Brain var5 = var2.getBrain();
      int var6 = (Integer)var5.getMemory(MemoryModuleType.PLAY_DEAD_TICKS).get();
      if (var6 <= 0) {
         var5.eraseMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         var5.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         var5.useDefaultActivity();
      } else {
         var5.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, (Object)(var6 - 1));
      }

   }
}
