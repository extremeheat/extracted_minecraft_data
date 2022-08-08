package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StayCloseToTarget<E extends LivingEntity> extends Behavior<E> {
   private final Function<LivingEntity, Optional<PositionTracker>> targetPositionGetter;
   private final int closeEnough;
   private final int tooFar;
   private final float speedModifier;

   public StayCloseToTarget(Function<LivingEntity, Optional<PositionTracker>> var1, int var2, int var3, float var4) {
      super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.targetPositionGetter = var1;
      this.closeEnough = var2;
      this.tooFar = var3;
      this.speedModifier = var4;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      Optional var3 = (Optional)this.targetPositionGetter.apply(var2);
      if (var3.isEmpty()) {
         return false;
      } else {
         PositionTracker var4 = (PositionTracker)var3.get();
         return !var2.position().closerThan(var4.currentPosition(), (double)this.tooFar);
      }
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(var2, (PositionTracker)((Optional)this.targetPositionGetter.apply(var2)).get(), this.speedModifier, this.closeEnough);
   }
}
