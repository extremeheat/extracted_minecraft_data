package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
   public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
   public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = (entity) -> entity.isAlive() && entity instanceof LivingEntity;
   public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = (entity) -> entity.isAlive() && !entity.isVehicle() && !entity.isPassenger();
   public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = (entity) -> entity instanceof Container && entity.isAlive();
   public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = (entity) -> {
      boolean var10000;
      if (entity instanceof Player player) {
         if (entity.isSpectator() || player.isCreative()) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   };
   public static final Predicate<Entity> NO_SPECTATORS = (entity) -> !entity.isSpectator();
   public static final Predicate<Entity> CAN_BE_COLLIDED_WITH;
   public static final Predicate<Entity> CAN_BE_PICKED;

   private EntitySelector() {
      super();
   }

   public static Predicate<Entity> withinDistance(final double centerX, final double centerY, final double centerZ, final double distance) {
      double distanceSqr = distance * distance;
      return (input) -> input.distanceToSqr(centerX, centerY, centerZ) <= distanceSqr;
   }

   public static Predicate<Entity> pushableBy(final Entity entity) {
      Team ownTeam = entity.getTeam();
      Team.CollisionRule ownCollisionRule = ownTeam == null ? Team.CollisionRule.ALWAYS : ownTeam.getCollisionRule();
      return (Predicate<Entity>)(ownCollisionRule == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NO_SPECTATORS.and((input) -> {
         if (!input.isPushable()) {
            return false;
         } else {
            if (entity.level().isClientSide()) {
               if (!(input instanceof Player)) {
                  return false;
               }

               Player player = (Player)input;
               if (!player.isLocalPlayer()) {
                  return false;
               }
            }

            Team theirTeam = input.getTeam();
            Team.CollisionRule theirCollisionRule = theirTeam == null ? Team.CollisionRule.ALWAYS : theirTeam.getCollisionRule();
            if (theirCollisionRule == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean sameTeam = ownTeam != null && ownTeam.isAlliedTo(theirTeam);
               if ((ownCollisionRule == Team.CollisionRule.PUSH_OWN_TEAM || theirCollisionRule == Team.CollisionRule.PUSH_OWN_TEAM) && sameTeam) {
                  return false;
               } else if ((ownCollisionRule == Team.CollisionRule.PUSH_OTHER_TEAMS || theirCollisionRule == Team.CollisionRule.PUSH_OTHER_TEAMS) && !sameTeam) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }));
   }

   public static Predicate<Entity> notRiding(final Entity entity) {
      return (input) -> {
         while(true) {
            if (input.isPassenger()) {
               input = input.getVehicle();
               if (input != entity) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   static {
      CAN_BE_COLLIDED_WITH = NO_SPECTATORS.and((entity) -> entity.canBeCollidedWith((Entity)null));
      CAN_BE_PICKED = Entity::isPickable;
   }
}
