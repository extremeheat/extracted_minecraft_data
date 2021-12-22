package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;

public class StrollAroundPoi extends Behavior<PathfinderMob> {
   private static final int MIN_TIME_BETWEEN_STROLLS = 180;
   private static final int STROLL_MAX_XZ_DIST = 8;
   private static final int STROLL_MAX_Y_DIST = 6;
   private final MemoryModuleType<GlobalPos> memoryType;
   private long nextOkStartTime;
   private final int maxDistanceFromPoi;
   private final float speedModifier;

   public StrollAroundPoi(MemoryModuleType<GlobalPos> var1, float var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, var1, MemoryStatus.VALUE_PRESENT));
      this.memoryType = var1;
      this.speedModifier = var2;
      this.maxDistanceFromPoi = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      Optional var3 = var2.getBrain().getMemory(this.memoryType);
      return var3.isPresent() && var1.dimension() == ((GlobalPos)var3.get()).dimension() && ((GlobalPos)var3.get()).pos().closerThan(var2.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 > this.nextOkStartTime) {
         Optional var5 = Optional.ofNullable(LandRandomPos.getPos(var2, 8, 6));
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((var1x) -> {
            return new WalkTarget(var1x, this.speedModifier, 1);
         }));
         this.nextOkStartTime = var3 + 180L;
      }

   }
}
