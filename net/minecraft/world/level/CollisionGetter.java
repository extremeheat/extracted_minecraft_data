package net.minecraft.world.level;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
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
      return this.noCollision((Entity)null, var1, Collections.emptySet());
   }

   default boolean noCollision(Entity var1) {
      return this.noCollision(var1, var1.getBoundingBox(), Collections.emptySet());
   }

   default boolean noCollision(Entity var1, AABB var2) {
      return this.noCollision(var1, var2, Collections.emptySet());
   }

   default boolean noCollision(@Nullable Entity var1, AABB var2, Set var3) {
      return this.getCollisions(var1, var2, var3).allMatch(VoxelShape::isEmpty);
   }

   default Stream getEntityCollisions(@Nullable Entity var1, AABB var2, Set var3) {
      return Stream.empty();
   }

   default Stream getCollisions(@Nullable Entity var1, AABB var2, Set var3) {
      return Streams.concat(new Stream[]{this.getBlockCollisions(var1, var2), this.getEntityCollisions(var1, var2, var3)});
   }

   default Stream getBlockCollisions(@Nullable final Entity var1, AABB var2) {
      int var3 = Mth.floor(var2.minX - 1.0E-7D) - 1;
      int var4 = Mth.floor(var2.maxX + 1.0E-7D) + 1;
      int var5 = Mth.floor(var2.minY - 1.0E-7D) - 1;
      int var6 = Mth.floor(var2.maxY + 1.0E-7D) + 1;
      int var7 = Mth.floor(var2.minZ - 1.0E-7D) - 1;
      int var8 = Mth.floor(var2.maxZ + 1.0E-7D) + 1;
      final CollisionContext var9 = var1 == null ? CollisionContext.empty() : CollisionContext.of(var1);
      final Cursor3D var10 = new Cursor3D(var3, var5, var7, var4, var6, var8);
      final BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      final VoxelShape var12 = Shapes.create(var2);
      return StreamSupport.stream(new AbstractSpliterator(Long.MAX_VALUE, 1280) {
         boolean checkedBorder = var1 == null;

         public boolean tryAdvance(Consumer var1x) {
            if (!this.checkedBorder) {
               this.checkedBorder = true;
               VoxelShape var2 = CollisionGetter.this.getWorldBorder().getCollisionShape();
               boolean var3 = Shapes.joinIsNotEmpty(var2, Shapes.create(var1.getBoundingBox().deflate(1.0E-7D)), BooleanOp.AND);
               boolean var4 = Shapes.joinIsNotEmpty(var2, Shapes.create(var1.getBoundingBox().inflate(1.0E-7D)), BooleanOp.AND);
               if (!var3 && var4) {
                  var1x.accept(var2);
                  return true;
               }
            }

            VoxelShape var11x;
            do {
               int var5;
               BlockState var9x;
               int var12x;
               int var13;
               int var14;
               do {
                  do {
                     BlockGetter var8;
                     do {
                        do {
                           if (!var10.advance()) {
                              return false;
                           }

                           var12x = var10.nextX();
                           var13 = var10.nextY();
                           var14 = var10.nextZ();
                           var5 = var10.getNextType();
                        } while(var5 == 3);

                        int var6 = var12x >> 4;
                        int var7 = var14 >> 4;
                        var8 = CollisionGetter.this.getChunkForCollisions(var6, var7);
                     } while(var8 == null);

                     var11.set(var12x, var13, var14);
                     var9x = var8.getBlockState(var11);
                  } while(var5 == 1 && !var9x.hasLargeCollisionShape());
               } while(var5 == 2 && var9x.getBlock() != Blocks.MOVING_PISTON);

               VoxelShape var10x = var9x.getCollisionShape(CollisionGetter.this, var11, var9);
               var11x = var10x.move((double)var12x, (double)var13, (double)var14);
            } while(!Shapes.joinIsNotEmpty(var12, var11x, BooleanOp.AND));

            var1x.accept(var11x);
            return true;
         }
      }, false);
   }
}
