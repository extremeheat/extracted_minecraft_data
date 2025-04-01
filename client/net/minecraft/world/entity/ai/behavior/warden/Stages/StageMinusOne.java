package net.minecraft.world.entity.ai.behavior.warden.Stages;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class StageMinusOne<E extends Warden> extends Behavior<E> {
   final Pose pose;
   final int NEXT_STAGE = 0;

   public StageMinusOne(int var1, Pose var2) {
      super(ImmutableMap.of(), var1);
      this.pose = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == -1;
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == -1;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.setPose(this.pose);
      var2.lookAt(EntityAnchorArgument.Anchor.EYES, var2.position().add(-1.0, 0.0, 0.0));
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (!this.stageCleared(var2)) {
         Optional var10000 = var2.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
         Objects.requireNonNull(var2);
         var10000.ifPresent(var2::setAttackTarget);
         var2.getBrain().eraseMemory(MemoryModuleType.ACTING_STAGE);
         var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
         var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      }

   }

   boolean stageCleared(E var1) {
      return var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 0;
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
