package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
   public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
   public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = (var0) -> {
      return var0.isAlive() && var0 instanceof LivingEntity;
   };
   public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = (var0) -> {
      return var0.isAlive() && !var0.isVehicle() && !var0.isPassenger();
   };
   public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = (var0) -> {
      return var0 instanceof Container && var0.isAlive();
   };
   public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = (var0) -> {
      return !(var0 instanceof Player) || !var0.isSpectator() && !((Player)var0).isCreative();
   };
   public static final Predicate<Entity> NO_SPECTATORS = (var0) -> {
      return !var0.isSpectator();
   };
   public static final Predicate<Entity> CAN_BE_COLLIDED_WITH;
   public static final Predicate<Entity> CAN_BE_PICKED;

   private EntitySelector() {
      super();
   }

   public static Predicate<Entity> withinDistance(double var0, double var2, double var4, double var6) {
      double var8 = var6 * var6;
      return (var8x) -> {
         return var8x != null && var8x.distanceToSqr(var0, var2, var4) <= var8;
      };
   }

   public static Predicate<Entity> pushableBy(Entity var0) {
      PlayerTeam var1 = var0.getTeam();
      Team.CollisionRule var2 = var1 == null ? Team.CollisionRule.ALWAYS : ((Team)var1).getCollisionRule();
      return (Predicate)(var2 == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NO_SPECTATORS.and((var3) -> {
         if (!var3.isPushable()) {
            return false;
         } else if (var0.level().isClientSide && (!(var3 instanceof Player) || !((Player)var3).isLocalPlayer())) {
            return false;
         } else {
            PlayerTeam var4 = var3.getTeam();
            Team.CollisionRule var5 = var4 == null ? Team.CollisionRule.ALWAYS : ((Team)var4).getCollisionRule();
            if (var5 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean var6 = var1 != null && var1.isAlliedTo(var4);
               if ((var2 == Team.CollisionRule.PUSH_OWN_TEAM || var5 == Team.CollisionRule.PUSH_OWN_TEAM) && var6) {
                  return false;
               } else {
                  return var2 != Team.CollisionRule.PUSH_OTHER_TEAMS && var5 != Team.CollisionRule.PUSH_OTHER_TEAMS || var6;
               }
            }
         }
      }));
   }

   public static Predicate<Entity> notRiding(Entity var0) {
      return (var1) -> {
         while(true) {
            if (var1.isPassenger()) {
               var1 = var1.getVehicle();
               if (var1 != var0) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   static {
      CAN_BE_COLLIDED_WITH = NO_SPECTATORS.and(Entity::canBeCollidedWith);
      CAN_BE_PICKED = NO_SPECTATORS.and(Entity::isPickable);
   }
}
