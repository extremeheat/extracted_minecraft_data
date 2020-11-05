package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;

public class RandomStroll extends Behavior<PathfinderMob> {
   private final float speedModifier;
   private final int maxHorizontalDistance;
   private final int maxVerticalDistance;

   public RandomStroll(float var1) {
      this(var1, 10, 7);
   }

   public RandomStroll(float var1, int var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
      this.maxHorizontalDistance = var2;
      this.maxVerticalDistance = var3;
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      Optional var5 = Optional.ofNullable(LandRandomPos.getPos(var2, this.maxHorizontalDistance, this.maxVerticalDistance));
      var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((var1x) -> {
         return new WalkTarget(var1x, this.speedModifier, 0);
      }));
   }
}
