package net.minecraft.world.level;

import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionGetter extends BlockGetter {
   WorldBorder getWorldBorder();

   @Nullable
   BlockGetter getChunkForCollisions(int var1, int var2);

   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      return true;
   }

   default boolean isUnobstructed(BlockState var1, BlockPos var2, CollisionContext var3) {
      VoxelShape var4 = var1.getCollisionShape(this, var2, var3);
      return var4.isEmpty() || this.isUnobstructed((Entity)null, var4.move((double)var2.getX(), (double)var2.getY(), (double)var2.getZ()));
   }

   default boolean isUnobstructed(Entity var1) {
      return this.isUnobstructed(var1, Shapes.create(var1.getBoundingBox()));
   }

   default boolean noCollision(AABB var1) {
      return this.noCollision((Entity)null, var1);
   }

   default boolean noCollision(Entity var1) {
      return this.noCollision(var1, var1.getBoundingBox());
   }

   default boolean noCollision(@Nullable Entity var1, AABB var2) {
      Iterator var3 = this.getBlockCollisions(var1, var2).iterator();

      while(var3.hasNext()) {
         VoxelShape var4 = (VoxelShape)var3.next();
         if (!var4.isEmpty()) {
            return false;
         }
      }

      if (!this.getEntityCollisions(var1, var2).isEmpty()) {
         return false;
      } else if (var1 == null) {
         return true;
      } else {
         VoxelShape var5 = this.borderCollision(var1, var2);
         return var5 == null || !Shapes.joinIsNotEmpty(var5, Shapes.create(var2), BooleanOp.AND);
      }
   }

   default boolean noBlockCollision(@Nullable Entity var1, AABB var2) {
      Iterator var3 = this.getBlockCollisions(var1, var2).iterator();

      VoxelShape var4;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         var4 = (VoxelShape)var3.next();
      } while(var4.isEmpty());

      return false;
   }

   List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2);

   default Iterable<VoxelShape> getCollisions(@Nullable Entity var1, AABB var2) {
      List var3 = this.getEntityCollisions(var1, var2);
      Iterable var4 = this.getBlockCollisions(var1, var2);
      return var3.isEmpty() ? var4 : Iterables.concat(var3, var4);
   }

   default Iterable<VoxelShape> getBlockCollisions(@Nullable Entity var1, AABB var2) {
      return () -> {
         return new BlockCollisions(this, var1, var2, false, (var0, var1x) -> {
            return var1x;
         });
      };
   }

   @Nullable
   private VoxelShape borderCollision(Entity var1, AABB var2) {
      WorldBorder var3 = this.getWorldBorder();
      return var3.isInsideCloseToBorder(var1, var2) ? var3.getCollisionShape() : null;
   }

   default boolean collidesWithSuffocatingBlock(@Nullable Entity var1, AABB var2) {
      BlockCollisions var3 = new BlockCollisions(this, var1, var2, true, (var0, var1x) -> {
         return var1x;
      });

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((VoxelShape)var3.next()).isEmpty());

      return true;
   }

   default Optional<BlockPos> findSupportingBlock(Entity var1, AABB var2) {
      BlockPos var3 = null;
      double var4 = 1.7976931348623157E308;
      BlockCollisions var6 = new BlockCollisions(this, var1, var2, false, (var0, var1x) -> {
         return var0;
      });

      while(true) {
         BlockPos var7;
         double var8;
         do {
            if (!var6.hasNext()) {
               return Optional.ofNullable(var3);
            }

            var7 = (BlockPos)var6.next();
            var8 = var7.distToCenterSqr(var1.position());
         } while(!(var8 < var4) && (var8 != var4 || var3 != null && var3.compareTo(var7) >= 0));

         var3 = var7.immutable();
         var4 = var8;
      }
   }

   default Optional<Vec3> findFreePosition(@Nullable Entity var1, VoxelShape var2, Vec3 var3, double var4, double var6, double var8) {
      if (var2.isEmpty()) {
         return Optional.empty();
      } else {
         AABB var10 = var2.bounds().inflate(var4, var6, var8);
         VoxelShape var11 = (VoxelShape)StreamSupport.stream(this.getBlockCollisions(var1, var10).spliterator(), false).filter((var1x) -> {
            return this.getWorldBorder() == null || this.getWorldBorder().isWithinBounds(var1x.bounds());
         }).flatMap((var0) -> {
            return var0.toAabbs().stream();
         }).map((var6x) -> {
            return var6x.inflate(var4 / 2.0, var6 / 2.0, var8 / 2.0);
         }).map(Shapes::create).reduce(Shapes.empty(), Shapes::or);
         VoxelShape var12 = Shapes.join(var2, var11, BooleanOp.ONLY_FIRST);
         return var12.closestPointTo(var3);
      }
   }
}
