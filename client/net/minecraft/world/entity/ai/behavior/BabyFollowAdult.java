package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BabyFollowAdult<E extends AgeableMob> extends Behavior<E> {
   private final UniformInt followRange;
   private final Function<LivingEntity, Float> speedModifier;

   public BabyFollowAdult(UniformInt var1, float var2) {
      this(var1, (var1x) -> {
         return var2;
      });
   }

   public BabyFollowAdult(UniformInt var1, Function<LivingEntity, Float> var2) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.followRange = var1;
      this.speedModifier = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      if (!var2.isBaby()) {
         return false;
      } else {
         AgeableMob var3 = this.getNearestAdult(var2);
         return var2.closerThan(var3, (double)(this.followRange.getMaxValue() + 1)) && !var2.closerThan(var3, (double)this.followRange.getMinValue());
      }
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(var2, (Entity)this.getNearestAdult(var2), (Float)this.speedModifier.apply(var2), this.followRange.getMinValue() - 1);
   }

   private AgeableMob getNearestAdult(E var1) {
      return (AgeableMob)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
   }
}
