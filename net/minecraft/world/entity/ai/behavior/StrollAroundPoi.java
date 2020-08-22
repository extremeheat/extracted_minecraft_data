package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;

public class StrollAroundPoi extends Behavior {
   private final MemoryModuleType memoryType;
   private long nextOkStartTime;
   private final int maxDistanceFromPoi;

   public StrollAroundPoi(MemoryModuleType var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, var1, MemoryStatus.VALUE_PRESENT));
      this.memoryType = var1;
      this.maxDistanceFromPoi = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      Optional var3 = var2.getBrain().getMemory(this.memoryType);
      return var3.isPresent() && Objects.equals(var1.getDimension().getType(), ((GlobalPos)var3.get()).dimension()) && ((GlobalPos)var3.get()).pos().closerThan(var2.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 > this.nextOkStartTime) {
         Optional var5 = Optional.ofNullable(RandomPos.getLandPos(var2, 8, 6));
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5.map((var0) -> {
            return new WalkTarget(var0, 0.4F, 1);
         }));
         this.nextOkStartTime = var3 + 180L;
      }

   }
}
