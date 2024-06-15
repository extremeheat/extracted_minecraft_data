package net.minecraft.world.entity.ai.behavior;

import java.util.Collections;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk {
   public InsideBrownianWalk() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(float var0) {
      return BehaviorBuilder.create(
         var1 -> var1.group(var1.absent(MemoryModuleType.WALK_TARGET))
               .apply(
                  var1,
                  var1x -> (var2, var3, var4) -> {
                        if (var2.canSeeSky(var3.blockPosition())) {
                           return false;
                        } else {
                           BlockPos var6 = var3.blockPosition();
                           List var7 = BlockPos.betweenClosedStream(var6.offset(-1, -1, -1), var6.offset(1, 1, 1))
                              .map(BlockPos::immutable)
                              .collect(Util.toMutableList());
                           Collections.shuffle(var7);
                           var7.stream()
                              .filter(var1xxx -> !var2.canSeeSky(var1xxx))
                              .filter(var2x -> var2.loadedAndEntityCanStandOn(var2x, var3))
                              .filter(var2x -> var2.noCollision(var3))
                              .findFirst()
                              .ifPresent(var2x -> var1x.set(new WalkTarget(var2x, var0, 0)));
                           return true;
                        }
                     }
               )
      );
   }
}
