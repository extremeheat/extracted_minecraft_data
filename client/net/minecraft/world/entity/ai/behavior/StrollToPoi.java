package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollToPoi {
   public StrollToPoi() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(final MemoryModuleType<GlobalPos> memoryType, final float speedModifier, final int closeEnoughDist, final int maxDistanceFromPoi) {
      MutableLong nextOkStartTime = new MutableLong(0L);
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.WALK_TARGET), i.present(memoryType)).apply(i, (walkTarget, memory) -> (level, body, timestamp) -> {
               GlobalPos pos = (GlobalPos)i.get(memory);
               if (level.dimension() == pos.dimension() && pos.pos().closerToCenterThan(body.position(), (double)maxDistanceFromPoi)) {
                  if (timestamp <= nextOkStartTime.longValue()) {
                     return true;
                  } else {
                     walkTarget.set(new WalkTarget(pos.pos(), speedModifier, closeEnoughDist));
                     nextOkStartTime.setValue(timestamp + 80L);
                     return true;
                  }
               } else {
                  return false;
               }
            })));
   }
}
