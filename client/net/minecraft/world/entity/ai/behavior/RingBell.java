package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RingBell {
   private static final float BELL_RING_CHANCE = 0.95F;
   public static final int RING_BELL_FROM_DISTANCE = 3;

   public RingBell() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.MEETING_POINT)).apply(i, (meetingPoint) -> (level, body, timestamp) -> {
               if (level.getRandom().nextFloat() <= 0.95F) {
                  return false;
               } else {
                  BlockPos pos = ((GlobalPos)i.get(meetingPoint)).pos();
                  if (pos.closerThan(body.blockPosition(), 3.0)) {
                     BlockState state = level.getBlockState(pos);
                     if (state.is(Blocks.BELL)) {
                        BellBlock bellBlock = (BellBlock)state.getBlock();
                        bellBlock.attemptToRing(body, level, pos, (Direction)null);
                     }
                  }

                  return true;
               }
            })));
   }
}
