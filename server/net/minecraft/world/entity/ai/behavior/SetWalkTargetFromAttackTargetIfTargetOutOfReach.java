package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromAttackTargetIfTargetOutOfReach extends Behavior<Mob> {
   private final float speedModifier;

   public SetWalkTargetFromAttackTargetIfTargetOutOfReach(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.REGISTERED));
      this.speedModifier = var1;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      LivingEntity var5 = (LivingEntity)var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      if (BehaviorUtils.canSee(var2, var5) && BehaviorUtils.isWithinAttackRange(var2, var5, 1)) {
         this.clearWalkTarget(var2);
      } else {
         this.setWalkAndLookTarget(var2, var5);
      }

   }

   private void setWalkAndLookTarget(LivingEntity var1, LivingEntity var2) {
      Brain var3 = var1.getBrain();
      var3.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var2, true)));
      WalkTarget var4 = new WalkTarget(new EntityTracker(var2, false), this.speedModifier, 0);
      var3.setMemory(MemoryModuleType.WALK_TARGET, (Object)var4);
   }

   private void clearWalkTarget(LivingEntity var1) {
      var1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }
}
