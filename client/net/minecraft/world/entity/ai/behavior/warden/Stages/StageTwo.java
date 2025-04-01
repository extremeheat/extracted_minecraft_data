package net.minecraft.world.entity.ai.behavior.warden.Stages;

import com.google.common.collect.ImmutableMap;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;

public class StageTwo<E extends Warden> extends Behavior<E> {
   final Vec3 pathfindingOffset;
   final Pose pose;
   final float WALK_SPEED_MODIFIER = 0.7F;
   final int NEXT_STAGE = 3;
   public boolean hasSentWormMessage = false;

   public StageTwo(int var1, Vec3 var2, Pose var3) {
      super(ImmutableMap.of(), var1);
      this.pathfindingOffset = var2;
      this.pose = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 2;
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      if (var1.players().size() > 0 && !this.hasSentWormMessage) {
         this.hasSentWormMessage = true;
         ((ServerPlayer)var1.players().get(0)).sendSystemMessage(Component.literal("IT'S DOING THE WORM! THE WORM!"), true);
      }

      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 2;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.setPose(this.pose);
      var2.lookAt(EntityAnchorArgument.Anchor.EYES, var2.position().add(-1.0, 0.0, 0.0));
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (this.stageCleared(var2)) {
         WalkTarget var5 = new WalkTarget(var2.position().add(this.pathfindingOffset), 0.7F, 0);
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var5);
         var2.getBrain().setMemory(MemoryModuleType.ACTING_STAGE, 3);
      } else {
         var2.stopActing();
      }

   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      if (!var2.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && var2.getPose() == Pose.SLEEPING) {
         var2.setPose(Pose.STANDING);
      }

      super.tick(var1, var2, var3);
   }

   boolean stageCleared(E var1) {
      return var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var1.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 3;
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
