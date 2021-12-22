package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RingBell extends Behavior<LivingEntity> {
   private static final float BELL_RING_CHANCE = 0.95F;
   public static final int RING_BELL_FROM_DISTANCE = 3;

   public RingBell() {
      super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return var1.random.nextFloat() > 0.95F;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      BlockPos var6 = ((GlobalPos)var5.getMemory(MemoryModuleType.MEETING_POINT).get()).pos();
      if (var6.closerThan(var2.blockPosition(), 3.0D)) {
         BlockState var7 = var1.getBlockState(var6);
         if (var7.is(Blocks.BELL)) {
            BellBlock var8 = (BellBlock)var7.getBlock();
            var8.attemptToRing(var2, var1, var6, (Direction)null);
         }
      }

   }
}
