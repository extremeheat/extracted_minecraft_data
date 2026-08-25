package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindLandNearLiquid {
   public TryFindLandNearLiquid() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(final int range, final float speedModifier, final TagKey<Fluid> fluidTag) {
      MutableLong nextOkStartTime = new MutableLong(0L);
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.ATTACK_TARGET), i.absent(MemoryModuleType.WALK_TARGET), i.registered(MemoryModuleType.LOOK_TARGET)).apply(i, (var4, walkTarget, lookTarget) -> (level, body, timestamp) -> {
               if (level.getFluidState(body.blockPosition()).is(fluidTag)) {
                  return false;
               } else if (timestamp < nextOkStartTime.longValue()) {
                  nextOkStartTime.setValue(timestamp + 40L);
                  return true;
               } else {
                  CollisionContext context = CollisionContext.of(body);
                  BlockPos bodyBlockPos = body.blockPosition();
                  BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();
                  level.findBlocksInBoxByManhattanDistance(bodyBlockPos, range).filterPos((pos) -> pos.differsHorizontally(bodyBlockPos)).findFirst((pos, state) -> {
                     if (!state.getCollisionShape(level, pos, context).isEmpty()) {
                        return false;
                     } else if (level.getBlockState(testPos.setWithOffset(pos, (Direction)Direction.DOWN)).getCollisionShape(level, pos, context).isEmpty()) {
                        return false;
                     } else {
                        for(Direction direction : Direction.Plane.HORIZONTAL) {
                           testPos.setWithOffset(pos, (Direction)direction);
                           if (level.getBlockState(testPos).isAir() && level.getBlockState(testPos.move(Direction.DOWN)).getFluidState().is(fluidTag)) {
                              return true;
                           }
                        }

                        return false;
                     }
                  }).ifPresent((pos) -> {
                     BlockPos targetPos = pos.immutable();
                     lookTarget.set(new BlockPosTracker(targetPos));
                     walkTarget.set(new WalkTarget(new BlockPosTracker(targetPos), speedModifier, 0));
                  });
                  nextOkStartTime.setValue(timestamp + 40L);
                  return true;
               }
            })));
   }
}
