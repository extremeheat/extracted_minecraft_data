package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindLand {
   private static final int COOLDOWN_TICKS = 60;

   public TryFindLand() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(final int range, final float speedModifier) {
      MutableLong nextOkStartTime = new MutableLong(0L);
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.ATTACK_TARGET), i.absent(MemoryModuleType.WALK_TARGET), i.registered(MemoryModuleType.LOOK_TARGET)).apply(i, (attackTarget, walkTarget, lookTarget) -> (level, body, timestamp) -> {
               if (!level.getFluidState(body.blockPosition()).is(FluidTags.WATER)) {
                  return false;
               } else if (timestamp < nextOkStartTime.longValue()) {
                  nextOkStartTime.setValue(timestamp + 60L);
                  return true;
               } else {
                  BlockPos bodyBlockPos = body.blockPosition();
                  BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();
                  CollisionContext context = CollisionContext.of(body);
                  level.findBlocksInBoxByManhattanDistance(bodyBlockPos, range).filterPos((pos) -> pos.differsHorizontally(bodyBlockPos)).filterState((state) -> state.getFluidState().isEmpty()).findFirst((pos, state) -> canStandOn(level, pos, state, context, belowPos)).ifPresent((pos) -> {
                     BlockPos targetPos = pos.immutable();
                     lookTarget.set(new BlockPosTracker(targetPos));
                     walkTarget.set(new WalkTarget(new BlockPosTracker(targetPos), speedModifier, 1));
                  });
                  nextOkStartTime.setValue(timestamp + 60L);
                  return true;
               }
            })));
   }

   private static boolean canStandOn(final ServerLevel level, final BlockPos pos, final BlockState state, final CollisionContext context, final BlockPos.MutableBlockPos belowPos) {
      return state.getCollisionShape(level, pos, context).isEmpty() && level.getBlockState(belowPos.setWithOffset(pos, (Direction)Direction.DOWN)).isFaceSturdy(level, belowPos, Direction.UP);
   }
}
