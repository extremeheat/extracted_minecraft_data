package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
   List<Entity> getEntities(@Nullable Entity var1, AABB var2, Predicate<? super Entity> var3);

   <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3);

   default <T extends Entity> List<T> getEntitiesOfClass(Class<T> var1, AABB var2, Predicate<? super T> var3) {
      return this.getEntities(EntityTypeTest.forClass(var1), var2, var3);
   }

   List<? extends Player> players();

   default List<Entity> getEntities(@Nullable Entity var1, AABB var2) {
      return this.getEntities(var1, var2, EntitySelector.NO_SPECTATORS);
   }

   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      if (var2.isEmpty()) {
         return true;
      } else {
         for(Entity var4 : this.getEntities(var1, var2.bounds())) {
            if (!var4.isRemoved() && var4.blocksBuilding && (var1 == null || !var4.isPassengerOfSameVehicle(var1)) && Shapes.joinIsNotEmpty(var2, Shapes.create(var4.getBoundingBox()), BooleanOp.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   default <T extends Entity> List<T> getEntitiesOfClass(Class<T> var1, AABB var2) {
      return this.<T>getEntitiesOfClass(var1, var2, EntitySelector.NO_SPECTATORS);
   }

   default List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2) {
      if (var2.getSize() < 1.0E-7) {
         return List.of();
      } else {
         Predicate var10000;
         if (var1 == null) {
            var10000 = EntitySelector.CAN_BE_COLLIDED_WITH;
         } else {
            var10000 = EntitySelector.NO_SPECTATORS;
            Objects.requireNonNull(var1);
            var10000 = var10000.and(var1::canCollideWith);
         }

         Predicate var3 = var10000;
         List var4 = this.getEntities(var1, var2.inflate(1.0E-7), var3);
         if (var4.isEmpty()) {
            return List.of();
         } else {
            ImmutableList.Builder var5 = ImmutableList.builderWithExpectedSize(var4.size());

            for(Entity var7 : var4) {
               var5.add(Shapes.create(var7.getBoundingBox()));
            }

            return var5.build();
         }
      }
   }

   @Nullable
   default Player getNearestPlayer(double var1, double var3, double var5, double var7, @Nullable Predicate<Entity> var9) {
      double var10 = -1.0;
      Player var12 = null;

      for(Player var14 : this.players()) {
         if (var9 == null || var9.test(var14)) {
            double var15 = var14.distanceToSqr(var1, var3, var5);
            if ((var7 < 0.0 || var15 < var7 * var7) && (var10 == -1.0 || var15 < var10)) {
               var10 = var15;
               var12 = var14;
            }
         }
      }

      return var12;
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
      for(Player var10 : this.players()) {
         if (EntitySelector.NO_SPECTATORS.test(var10) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(var10)) {
            double var11 = var10.distanceToSqr(var1, var3, var5);
            if (var7 < 0.0 || var11 < var7 * var7) {
               return true;
            }
         }
      }

      return false;
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
