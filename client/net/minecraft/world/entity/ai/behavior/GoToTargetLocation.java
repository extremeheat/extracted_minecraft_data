package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GoToTargetLocation<E extends Mob> extends Behavior<E> {
   private final MemoryModuleType<BlockPos> locationMemory;
   private final int closeEnoughDist;
   private final float speedModifier;

   public GoToTargetLocation(MemoryModuleType<BlockPos> var1, int var2, float var3) {
      super(
         ImmutableMap.of(
            var1,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.LOOK_TARGET,
            MemoryStatus.REGISTERED
         )
      );
      this.locationMemory = var1;
      this.closeEnoughDist = var2;
      this.speedModifier = var3;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      BlockPos var5 = this.getTargetLocation(var2);
      boolean var6 = var5.closerThan(var2.blockPosition(), (double)this.closeEnoughDist);
      if (!var6) {
         BehaviorUtils.setWalkAndLookTargetMemories(var2, getNearbyPos(var2, var5), this.speedModifier, this.closeEnoughDist);
      }
   }

   private static BlockPos getNearbyPos(Mob var0, BlockPos var1) {
      RandomSource var2 = var0.level.random;
      return var1.offset(getRandomOffset(var2), 0, getRandomOffset(var2));
   }

   private static int getRandomOffset(RandomSource var0) {
      return var0.nextInt(3) - 1;
   }

   private BlockPos getTargetLocation(Mob var1) {
      return var1.getBrain().getMemory(this.locationMemory).get();
   }
}
