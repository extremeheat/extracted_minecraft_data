package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StrollToPoi extends Behavior<PathfinderMob> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private final float speedModifier;
   private long nextOkStartTime;

   public StrollToPoi(MemoryModuleType<GlobalPos> var1, float var2, int var3, int var4) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, var1, MemoryStatus.VALUE_PRESENT));
      this.memoryType = var1;
      this.speedModifier = var2;
      this.closeEnoughDist = var3;
      this.maxDistanceFromPoi = var4;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      Optional var3 = var2.getBrain().getMemory(this.memoryType);
      return var3.isPresent() && var1.dimension() == ((GlobalPos)var3.get()).dimension() && ((GlobalPos)var3.get()).pos().closerThan(var2.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 > this.nextOkStartTime) {
         Brain var5 = var2.getBrain();
         Optional var6 = var5.getMemory(this.memoryType);
         var6.ifPresent((var2x) -> {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2x.pos(), this.speedModifier, this.closeEnoughDist)));
         });
         this.nextOkStartTime = var3 + 80L;
      }

   }
}
