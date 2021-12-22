package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
      Team var1 = var0.getTeam();
      Team.CollisionRule var2 = var1 == null ? Team.CollisionRule.ALWAYS : var1.getCollisionRule();
      return (Predicate)(var2 == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NO_SPECTATORS.and((var3) -> {
         if (!var3.isPushable()) {
            return false;
         } else if (var0.level.isClientSide && (!(var3 instanceof Player) || !((Player)var3).isLocalPlayer())) {
            return false;
         } else {
            Team var4 = var3.getTeam();
            Team.CollisionRule var5 = var4 == null ? Team.CollisionRule.ALWAYS : var4.getCollisionRule();
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
   }

   public static class MobCanWearArmorEntitySelector implements Predicate<Entity> {
      private final ItemStack itemStack;

      public MobCanWearArmorEntitySelector(ItemStack var1) {
         super();
         this.itemStack = var1;
      }

      public boolean test(@Nullable Entity var1) {
         if (!var1.isAlive()) {
            return false;
         } else if (!(var1 instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity var2 = (LivingEntity)var1;
            return var2.canTakeItem(this.itemStack);
         }
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Entity)var1);
      }
   }
}
