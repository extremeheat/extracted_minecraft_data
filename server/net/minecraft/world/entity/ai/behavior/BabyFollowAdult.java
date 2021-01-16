package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BabyFollowAdult<E extends AgableMob> extends Behavior<E> {
   private final IntRange followRange;
   private final float speedModifier;

   public BabyFollowAdult(IntRange var1, float var2) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.followRange = var1;
      this.speedModifier = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      if (!var2.isBaby()) {
         return false;
      } else {
         AgableMob var3 = this.getNearestAdult(var2);
         return var2.closerThan(var3, (double)(this.followRange.getMaxInclusive() + 1)) && !var2.closerThan(var3, (double)this.followRange.getMinInclusive());
      }
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(var2, (Entity)this.getNearestAdult(var2), this.speedModifier, this.followRange.getMinInclusive() - 1);
   }

   private AgableMob getNearestAdult(E var1) {
      return (AgableMob)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
   }
}
