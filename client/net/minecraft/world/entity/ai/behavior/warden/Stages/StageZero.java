package net.minecraft.world.entity.ai.behavior.warden.Stages;

import com.google.common.collect.ImmutableMap;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;

public class StageZero<E extends Warden> extends Behavior<E> {
   final Vec3 pathfindingOffset;
   final Pose pose;
   final float WALK_SPEED_MODIFIER = 0.7F;
   final int NEXT_STAGE = 1;

   public StageZero(int var1, Vec3 var2, Pose var3) {
      super(ImmutableMap.of(), var1);
      this.pathfindingOffset = var2;
      this.pose = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 0;
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 0;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.setPose(this.pose);
      var2.lookAt(EntityAnchorArgument.Anchor.EYES, var2.position().add(-1.0, 0.0, 0.0));
      var1.getLevel().playSound((Entity)null, var1.getLevel().WARDEN_ARENA_POS, SoundEvents.VILLAGER_CROWD_START, SoundSource.MASTER, 10.0F, 1.0F);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (this.stageCleared(var2)) {
         WalkTarget var5 = new WalkTarget(var2.position().add(this.pathfindingOffset), 0.7F, 0);
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5);
         var2.getBrain().setMemory(MemoryModuleType.ACTING_STAGE, 1);
      } else {
         if (var1.players().size() > 0) {
            ((ServerPlayer)var1.players().get(0)).sendSystemMessage(Component.literal("Even the Warden got too tired of waiting for the music to start..."), true);
         }

         var2.stopActing();
      }

   }

   boolean stageCleared(E var1) {
      return var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 1;
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
