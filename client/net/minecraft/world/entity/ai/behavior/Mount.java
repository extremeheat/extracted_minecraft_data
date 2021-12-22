package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Mount<E extends LivingEntity> extends Behavior<E> {
   private static final int CLOSE_ENOUGH_TO_START_RIDING_DIST = 1;
   private final float speedModifier;

   public Mount(float var1) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryStatus.VALUE_PRESENT));
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return !var2.isPassenger();
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      if (this.isCloseEnoughToStartRiding(var2)) {
         var2.startRiding(this.getRidableEntity(var2));
      } else {
         BehaviorUtils.setWalkAndLookTargetMemories(var2, (Entity)this.getRidableEntity(var2), this.speedModifier, 1);
      }

   }

   private boolean isCloseEnoughToStartRiding(E var1) {
      return this.getRidableEntity(var1).closerThan(var1, 1.0D);
   }

   private Entity getRidableEntity(E var1) {
      return (Entity)var1.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
   }
}
