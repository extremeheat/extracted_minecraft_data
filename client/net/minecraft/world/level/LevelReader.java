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
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface LevelReader extends BlockAndBiomeGetter {
   default boolean isEmptyBlock(BlockPos var1) {
      return this.getBlockState(var1).isAir();
   }

   default boolean canSeeSkyFromBelowWater(BlockPos var1) {
      if (var1.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(var1);
      } else {
         BlockPos var2 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ());
         if (!this.canSeeSky(var2)) {
            return false;
         } else {
            for(var2 = var2.below(); var2.getY() > var1.getY(); var2 = var2.below()) {
               BlockState var3 = this.getBlockState(var2);
               if (var3.getLightBlock(this, var2) > 0 && !var3.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   int getRawBrightness(BlockPos var1, int var2);

   @Nullable
   ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   @Deprecated
   boolean hasChunk(int var1, int var2);

   BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   default float getBrightness(BlockPos var1) {
      return this.getDimension().getBrightnessRamp()[this.getMaxLocalRawBrightness(var1)];
   }

   int getSkyDarken();

   WorldBorder getWorldBorder();

   boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2);

   default int getDirectSignal(BlockPos var1, Direction var2) {
      return this.getBlockState(var1).getDirectSignal(this, var1, var2);
   }

   boolean isClientSide();

   int getSeaLevel();

   default ChunkAccess getChunk(BlockPos var1) {
      return this.getChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   default ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, true);
   }

   default ChunkAccess getChunk(int var1, int var2, ChunkStatus var3) {
      return this.getChunk(var1, var2, var3, true);
   }

   default ChunkStatus statusForCollisions() {
      return ChunkStatus.EMPTY;
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

   default boolean noCollision(@Nullable Entity var1, AABB var2, Set<Entity> var3) {
      return this.getCollisions(var1, var2, var3).allMatch(VoxelShape::isEmpty);
   }

   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2, Set<Entity> var3) {
      return Stream.empty();
   }

   default Stream<VoxelShape> getCollisions(@Nullable Entity var1, AABB var2, Set<Entity> var3) {
      return Streams.concat(new Stream[]{this.getBlockCollisions(var1, var2), this.getEntityCollisions(var1, var2, var3)});
   }

   default Stream<VoxelShape> getBlockCollisions(@Nullable final Entity var1, AABB var2) {
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
      return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(9223372036854775807L, 1280) {
         boolean checkedBorder = var1 == null;

         public boolean tryAdvance(Consumer<? super VoxelShape> var1x) {
            if (!this.checkedBorder) {
               this.checkedBorder = true;
               VoxelShape var2 = LevelReader.this.getWorldBorder().getCollisionShape();
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
                     ChunkAccess var8;
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
                        var8 = LevelReader.this.getChunk(var6, var7, LevelReader.this.statusForCollisions(), false);
                     } while(var8 == null);

                     var11.set(var12x, var13, var14);
                     var9x = var8.getBlockState(var11);
                  } while(var5 == 1 && !var9x.hasLargeCollisionShape());
               } while(var5 == 2 && var9x.getBlock() != Blocks.MOVING_PISTON);

               VoxelShape var10x = var9x.getCollisionShape(LevelReader.this, var11, var9);
               var11x = var10x.move((double)var12x, (double)var13, (double)var14);
            } while(!Shapes.joinIsNotEmpty(var12, var11x, BooleanOp.AND));

            var1x.accept(var11x);
            return true;
         }
      }, false);
   }

   default boolean isWaterAt(BlockPos var1) {
      return this.getFluidState(var1).is(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  BlockState var13 = this.getBlockState(var8.set(var10, var11, var12));
                  if (!var13.getFluidState().isEmpty()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }

      return false;
   }

   default int getMaxLocalRawBrightness(BlockPos var1) {
      return this.getMaxLocalRawBrightness(var1, this.getSkyDarken());
   }

   default int getMaxLocalRawBrightness(BlockPos var1, int var2) {
      return var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000 ? this.getRawBrightness(var1, var2) : 15;
   }

   @Deprecated
   default boolean hasChunkAt(BlockPos var1) {
      return this.hasChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   @Deprecated
   default boolean hasChunksAt(BlockPos var1, BlockPos var2) {
      return this.hasChunksAt(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ());
   }

   @Deprecated
   default boolean hasChunksAt(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var3; var8 <= var6; ++var8) {
               if (!this.hasChunk(var7, var8)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Dimension getDimension();
}
