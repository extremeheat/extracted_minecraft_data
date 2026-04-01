package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record BackAwayFromAttackBehavior(Intent<Target> moveTowards) implements LivingBlockBehavior {
   public static final double BACK_AWAY_DISTANCE = 5.0;
   private static final int BACK_AWAY_COOLDOWN_TICKS = 200;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType((Function)((a) -> new BackAwayFromAttackBehavior(a.withIntentTo(LivingBlock.MOVE_TOWARDS))));

   public BackAwayFromAttackBehavior {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.getAttackedBy() != null;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (entity.getAttackedBy() != null) {
         Vec3 targetPos = getTargetPosition(entity.position(), entity.getAttackedBy().position());
         this.moveTowards.update(Target.near(targetPos, 0.1));
         entity.lastAttackedTick = tickCount;
         entity.setAttackedBy((Player)null);
         return true;
      } else {
         return entity.lastAttackedTick + 200 >= tickCount;
      }
   }

   private static Vec3 getTargetPosition(final Vec3 entityPos, final Vec3 playerPos) {
      double dx = entityPos.x() - playerPos.x();
      double dz = entityPos.z() - playerPos.z();
      double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
      if (horizontalDistance < 0.01) {
         return new Vec3(entityPos.x() + 5.0, entityPos.y(), entityPos.z());
      } else {
         double scale = 5.0 / horizontalDistance;
         return new Vec3(entityPos.x() + dx * scale, entityPos.y(), entityPos.z() + dz * scale);
      }
   }
}
