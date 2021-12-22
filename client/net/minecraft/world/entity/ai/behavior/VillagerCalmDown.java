package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerCalmDown extends Behavior<Villager> {
   private static final int SAFE_DISTANCE_FROM_DANGER = 36;

   public VillagerCalmDown() {
      super(ImmutableMap.of());
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      boolean var5 = VillagerPanicTrigger.isHurt(var2) || VillagerPanicTrigger.hasHostile(var2) || isCloseToEntityThatHurtMe(var2);
      if (!var5) {
         var2.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
         var2.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         var2.getBrain().updateActivityFromSchedule(var1.getDayTime(), var1.getGameTime());
      }

   }

   private static boolean isCloseToEntityThatHurtMe(Villager var0) {
      return var0.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((var1) -> {
         return var1.distanceToSqr(var0) <= 36.0D;
      }).isPresent();
   }
}
