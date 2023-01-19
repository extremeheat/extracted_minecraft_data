package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromLookTarget extends Behavior<LivingEntity> {
   private final Function<LivingEntity, Float> speedModifier;
   private final int closeEnoughDistance;
   private final Predicate<LivingEntity> canSetWalkTargetPredicate;

   public SetWalkTargetFromLookTarget(float var1, int var2) {
      this(var0 -> true, var1x -> var1, var2);
   }

   public SetWalkTargetFromLookTarget(Predicate<LivingEntity> var1, Function<LivingEntity, Float> var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT));
      this.speedModifier = var2;
      this.closeEnoughDistance = var3;
      this.canSetWalkTargetPredicate = var1;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return this.canSetWalkTargetPredicate.test(var2);
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      PositionTracker var6 = var5.getMemory(MemoryModuleType.LOOK_TARGET).get();
      var5.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(var6, this.speedModifier.apply(var2), this.closeEnoughDistance));
   }
}
