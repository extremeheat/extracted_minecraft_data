package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk extends Behavior<PathfinderMob> {
   private final float speedModifier;

   public InsideBrownianWalk(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return !var1.canSeeSky(var2.blockPosition());
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      BlockPos var5 = var2.blockPosition();
      List var6 = (List)BlockPos.betweenClosedStream(var5.offset(-1, -1, -1), var5.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
      Collections.shuffle(var6);
      Optional var7 = var6.stream().filter((var1x) -> {
         return !var1.canSeeSky(var1x);
      }).filter((var2x) -> {
         return var1.loadedAndEntityCanStandOn(var2x, var2);
      }).filter((var2x) -> {
         return var1.noCollision(var2);
      }).findFirst();
      var7.ifPresent((var2x) -> {
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2x, this.speedModifier, 0)));
      });
   }
}
