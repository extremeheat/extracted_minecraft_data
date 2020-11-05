package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GoToCelebrateLocation<E extends Mob> extends Behavior<E> {
   private final int closeEnoughDist;
   private final float speedModifier;

   public GoToCelebrateLocation(int var1, float var2) {
      super(ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
      this.closeEnoughDist = var1;
      this.speedModifier = var2;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      BlockPos var5 = getCelebrateLocation(var2);
      boolean var6 = var5.closerThan(var2.blockPosition(), (double)this.closeEnoughDist);
      if (!var6) {
         BehaviorUtils.setWalkAndLookTargetMemories(var2, (BlockPos)getNearbyPos(var2, var5), this.speedModifier, this.closeEnoughDist);
      }

   }

   private static BlockPos getNearbyPos(Mob var0, BlockPos var1) {
      Random var2 = var0.level.random;
      return var1.offset(getRandomOffset(var2), 0, getRandomOffset(var2));
   }

   private static int getRandomOffset(Random var0) {
      return var0.nextInt(3) - 1;
   }

   private static BlockPos getCelebrateLocation(Mob var0) {
      return (BlockPos)var0.getBrain().getMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
   }
}
