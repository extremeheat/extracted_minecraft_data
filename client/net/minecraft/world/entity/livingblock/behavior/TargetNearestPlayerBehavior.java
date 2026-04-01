package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TargetNearestPlayerBehavior implements LivingBlockBehavior {
   private static final int PLAYER_REEVALUATION_TICKS = 10;
   private static final double PLAYER_DISTANCE = 2.0;
   private final Intent<Target> moveTowards;
   private final double maxDistance;
   private final boolean targetEntity;
   private int lastTargetedTick = 0;

   public TargetNearestPlayerBehavior(final Intent<Target> moveTowards, final double maxDistance, final boolean targetEntity) {
      super();
      this.moveTowards = moveTowards;
      this.maxDistance = maxDistance;
      this.targetEntity = targetEntity;
   }

   public static LivingBlockBehaviorType targetNearestPlayer(final double maxDistance, final boolean targetEntity) {
      return LivingBlockBehaviorType.behaviorType((Function)((a) -> new TargetNearestPlayerBehavior(a.withIntentTo(LivingBlock.MOVE_TOWARDS), maxDistance, targetEntity)));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.isIdle() && this.lastTargetedTick + 10 < entity.tickCount && entity.lastTargetedTick + 10 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Vec3 pos = entity.position();
      Player player = level.getNearestPlayer(pos.x(), pos.y(), pos.z(), this.maxDistance, entity, this::targetCondition);
      Target target;
      if (!this.targetEntity && player != null) {
         target = Target.near(player.position(), 2.0);
      } else {
         target = Target.nearEntity(player, 2.0);
      }

      this.moveTowards.update(target);
      if (player != null) {
         entity.lastTargetedTick = tickCount;
      }

      this.lastTargetedTick = tickCount;
      return false;
   }

   public boolean targetCondition(final Entity entity, final LivingBlock livingBlock) {
      return true;
   }

   public double maxDistance() {
      return this.maxDistance;
   }
}
