package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;

public class BarrelRollBehavior implements LivingBlockBehavior {
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType(BarrelRollBehavior::new);
   private static final int REEVALUATION_TICKS = 10;
   private BlockPos lastBlockPos;
   private int lastTriggeredTick;

   public BarrelRollBehavior() {
      super();
      this.lastBlockPos = BlockPos.ZERO;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount - this.lastTriggeredTick >= 10;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      BlockPos currentPos = entity.blockPosition();
      if (!currentPos.equals(this.lastBlockPos)) {
         this.lastBlockPos = currentPos;
         this.triggerForPlayer(entity, level);
      }

      return false;
   }

   private void triggerForPlayer(final LivingBlock entity, final ServerLevel level) {
      ServerPlayer owner = entity.getAttributablePlayer();
      if (owner != null) {
         CriteriaTriggers.BARREL_ROLL.trigger(owner);
      } else {
         Target movementTarget = entity.getMovementTarget();
         if (movementTarget instanceof Target.EntityTarget) {
            Target.EntityTarget entityTarget = (Target.EntityTarget)movementTarget;
            Entity targetEntity = (Entity)entityTarget.entityReference().getEntity(level, Entity.class);
            if (targetEntity instanceof ServerPlayer) {
               ServerPlayer followedPlayer = (ServerPlayer)targetEntity;
               CriteriaTriggers.BARREL_ROLL.trigger(followedPlayer);
            }
         }

      }
   }
}
