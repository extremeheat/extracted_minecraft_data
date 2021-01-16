package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
   List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3);

   <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3);

   default <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3) {
      return this.getEntitiesOfClass(var1, var2, var3);
   }

   List<? extends Player> players();

   default List<Entity> getEntities(@Nullable Entity var1, AABB var2) {
      return this.getEntities(var1, var2, EntitySelector.NO_SPECTATORS);
   }

   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      if (var2.isEmpty()) {
         return true;
      } else {
         Iterator var3 = this.getEntities(var1, var2.bounds()).iterator();

         Entity var4;
         do {
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     var4 = (Entity)var3.next();
                  } while(var4.removed);
               } while(!var4.blocksBuilding);
            } while(var1 != null && var4.isPassengerOfSameVehicle(var1));
         } while(!Shapes.joinIsNotEmpty(var2, Shapes.create(var4.getBoundingBox()), BooleanOp.AND));

         return false;
      }
   }

   default <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> var1, AABB var2) {
      return this.getEntitiesOfClass(var1, var2, EntitySelector.NO_SPECTATORS);
   }

   default <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> var1, AABB var2) {
      return this.getLoadedEntitiesOfClass(var1, var2, EntitySelector.NO_SPECTATORS);
   }

   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2, Predicate<Entity> var3) {
      if (var2.getSize() < 1.0E-7D) {
         return Stream.empty();
      } else {
         AABB var4 = var2.inflate(1.0E-7D);
         return this.getEntities(var1, var4, var3.and((var2x) -> {
            boolean var10000;
            label25: {
               if (var2x.getBoundingBox().intersects(var4)) {
                  if (var1 == null) {
                     if (var2x.canBeCollidedWith()) {
                        break label25;
                     }
                  } else if (var1.canCollideWith(var2x)) {
                     break label25;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         })).stream().map(Entity::getBoundingBox).map(Shapes::create);
      }
   }

   @Nullable
   default Player getNearestPlayer(double var1, double var3, double var5, double var7, @Nullable Predicate<Entity> var9) {
      double var10 = -1.0D;
      Player var12 = null;
      Iterator var13 = this.players().iterator();

      while(true) {
         Player var14;
         double var15;
         do {
            do {
               do {
                  if (!var13.hasNext()) {
                     return var12;
                  }

                  var14 = (Player)var13.next();
               } while(var9 != null && !var9.test(var14));

               var15 = var14.distanceToSqr(var1, var3, var5);
            } while(var7 >= 0.0D && var15 >= var7 * var7);
         } while(var10 != -1.0D && var15 >= var10);

         var10 = var15;
         var12 = var14;
      }
   }

   @Nullable
   default Player getNearestPlayer(Entity var1, double var2) {
      return this.getNearestPlayer(var1.getX(), var1.getY(), var1.getZ(), var2, false);
   }

   @Nullable
   default Player getNearestPlayer(double var1, double var3, double var5, double var7, boolean var9) {
      Predicate var10 = var9 ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
      return this.getNearestPlayer(var1, var3, var5, var7, var10);
   }

   default boolean hasNearbyAlivePlayer(double var1, double var3, double var5, double var7) {
      Iterator var9 = this.players().iterator();

      double var11;
      do {
         Player var10;
         do {
            do {
               if (!var9.hasNext()) {
                  return false;
               }

               var10 = (Player)var9.next();
            } while(!EntitySelector.NO_SPECTATORS.test(var10));
         } while(!EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(var10));

         var11 = var10.distanceToSqr(var1, var3, var5);
      } while(var7 >= 0.0D && var11 >= var7 * var7);

      return true;
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, LivingEntity var2) {
      return (Player)this.getNearestEntity(this.players(), var1, var2, var2.getX(), var2.getY(), var2.getZ());
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, LivingEntity var2, double var3, double var5, double var7) {
      return (Player)this.getNearestEntity(this.players(), var1, var2, var3, var5, var7);
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, double var2, double var4, double var6) {
      return (Player)this.getNearestEntity(this.players(), var1, (LivingEntity)null, var2, var4, var6);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(Class<? extends T> var1, TargetingConditions var2, @Nullable LivingEntity var3, double var4, double var6, double var8, AABB var10) {
      return this.getNearestEntity(this.getEntitiesOfClass(var1, var10, (Predicate)null), var2, var3, var4, var6, var8);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestLoadedEntity(Class<? extends T> var1, TargetingConditions var2, @Nullable LivingEntity var3, double var4, double var6, double var8, AABB var10) {
      return this.getNearestEntity(this.getLoadedEntitiesOfClass(var1, var10, (Predicate)null), var2, var3, var4, var6, var8);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(List<? extends T> var1, TargetingConditions var2, @Nullable LivingEntity var3, double var4, double var6, double var8) {
      double var10 = -1.0D;
      LivingEntity var12 = null;
      Iterator var13 = var1.iterator();

      while(true) {
         LivingEntity var14;
         double var15;
         do {
            do {
               if (!var13.hasNext()) {
                  return var12;
               }

               var14 = (LivingEntity)var13.next();
            } while(!var2.test(var3, var14));

            var15 = var14.distanceToSqr(var4, var6, var8);
         } while(var10 != -1.0D && var15 >= var10);

         var10 = var15;
         var12 = var14;
      }
   }

   default List<Player> getNearbyPlayers(TargetingConditions var1, LivingEntity var2, AABB var3) {
      ArrayList var4 = Lists.newArrayList();
      Iterator var5 = this.players().iterator();

      while(var5.hasNext()) {
         Player var6 = (Player)var5.next();
         if (var3.contains(var6.getX(), var6.getY(), var6.getZ()) && var1.test(var2, var6)) {
            var4.add(var6);
         }
      }

      return var4;
   }

   default <T extends LivingEntity> List<T> getNearbyEntities(Class<? extends T> var1, TargetingConditions var2, LivingEntity var3, AABB var4) {
      List var5 = this.getEntitiesOfClass(var1, var4, (Predicate)null);
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = var5.iterator();

      while(var7.hasNext()) {
         LivingEntity var8 = (LivingEntity)var7.next();
         if (var2.test(var3, var8)) {
            var6.add(var8);
         }
      }

      return var6;
   }

   @Nullable
   default Player getPlayerByUUID(UUID var1) {
      for(int var2 = 0; var2 < this.players().size(); ++var2) {
         Player var3 = (Player)this.players().get(var2);
         if (var1.equals(var3.getUUID())) {
            return var3;
         }
      }

      return null;
   }
}
