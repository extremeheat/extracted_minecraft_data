package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public interface BlockGetter extends LevelHeightAccessor {
   @Nullable BlockEntity getBlockEntity(BlockPos var1);

   default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos var1, BlockEntityType<T> var2) {
      BlockEntity var3 = this.getBlockEntity(var1);
      return var3 != null && var3.getType() == var2 ? Optional.of(var3) : Optional.empty();
   }

   BlockState getBlockState(BlockPos var1);

   FluidState getFluidState(BlockPos var1);

   default int getLightEmission(BlockPos var1) {
      return this.getBlockState(var1).getLightEmission();
   }

   default Stream<BlockState> getBlockStates(AABB var1) {
      return BlockPos.betweenClosedStream(var1).map(this::getBlockState);
   }

   default BlockHitResult isBlockInLine(ClipBlockStateContext var1) {
      return (BlockHitResult)traverseBlocks(var1.getFrom(), var1.getTo(), var1, (var1x, var2) -> {
         BlockState var3 = this.getBlockState(var2);
         Vec3 var4 = var1x.getFrom().subtract(var1x.getTo());
         return var1x.isTargetBlock().test(var3) ? new BlockHitResult(var1x.getTo(), Direction.getApproximateNearest(var4.x, var4.y, var4.z), BlockPos.containing(var1x.getTo()), false) : null;
      }, (var0) -> {
         Vec3 var1 = var0.getFrom().subtract(var0.getTo());
         return BlockHitResult.miss(var0.getTo(), Direction.getApproximateNearest(var1.x, var1.y, var1.z), BlockPos.containing(var0.getTo()));
      });
   }

   default BlockHitResult clip(ClipContext var1) {
      return (BlockHitResult)traverseBlocks(var1.getFrom(), var1.getTo(), var1, (var1x, var2) -> {
         BlockState var3 = this.getBlockState(var2);
         FluidState var4 = this.getFluidState(var2);
         Vec3 var5 = var1x.getFrom();
         Vec3 var6 = var1x.getTo();
         VoxelShape var7 = var1x.getBlockShape(var3, this, var2);
         BlockHitResult var8 = this.clipWithInteractionOverride(var5, var6, var2, var7, var3);
         VoxelShape var9 = var1x.getFluidShape(var4, this, var2);
         BlockHitResult var10 = var9.clip(var5, var6, var2);
         double var11 = var8 == null ? 1.7976931348623157E308 : var1x.getFrom().distanceToSqr(var8.getLocation());
         double var13 = var10 == null ? 1.7976931348623157E308 : var1x.getFrom().distanceToSqr(var10.getLocation());
         return var11 <= var13 ? var8 : var10;
      }, (var0) -> {
         Vec3 var1 = var0.getFrom().subtract(var0.getTo());
         return BlockHitResult.miss(var0.getTo(), Direction.getApproximateNearest(var1.x, var1.y, var1.z), BlockPos.containing(var0.getTo()));
      });
   }

   default @Nullable BlockHitResult clipWithInteractionOverride(Vec3 var1, Vec3 var2, BlockPos var3, VoxelShape var4, BlockState var5) {
      BlockHitResult var6 = var4.clip(var1, var2, var3);
      if (var6 != null) {
         BlockHitResult var7 = var5.getInteractionShape(this, var3).clip(var1, var2, var3);
         if (var7 != null && var7.getLocation().subtract(var1).lengthSqr() < var6.getLocation().subtract(var1).lengthSqr()) {
            return var6.withDirection(var7.getDirection());
         }
      }

      return var6;
   }

   default double getBlockFloorHeight(VoxelShape var1, Supplier<VoxelShape> var2) {
      if (!var1.isEmpty()) {
         return var1.max(Direction.Axis.Y);
      } else {
         double var3 = ((VoxelShape)var2.get()).max(Direction.Axis.Y);
         return var3 >= 1.0 ? var3 - 1.0 : -1.0 / 0.0;
      }
   }

   default double getBlockFloorHeight(BlockPos var1) {
      return this.getBlockFloorHeight(this.getBlockState(var1).getCollisionShape(this, var1), () -> {
         BlockPos var2 = var1.below();
         return this.getBlockState(var2).getCollisionShape(this, var2);
      });
   }

   static <T, C> T traverseBlocks(Vec3 var0, Vec3 var1, C var2, BiFunction<C, BlockPos, @Nullable T> var3, Function<C, T> var4) {
      if (var0.equals(var1)) {
         return (T)var4.apply(var2);
      } else {
         double var5 = Mth.lerp(-1.0E-7, var1.x, var0.x);
         double var7 = Mth.lerp(-1.0E-7, var1.y, var0.y);
         double var9 = Mth.lerp(-1.0E-7, var1.z, var0.z);
         double var11 = Mth.lerp(-1.0E-7, var0.x, var1.x);
         double var13 = Mth.lerp(-1.0E-7, var0.y, var1.y);
         double var15 = Mth.lerp(-1.0E-7, var0.z, var1.z);
         int var17 = Mth.floor(var11);
         int var18 = Mth.floor(var13);
         int var19 = Mth.floor(var15);
         BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos(var17, var18, var19);
         Object var21 = var3.apply(var2, var20);
         if (var21 != null) {
            return (T)var21;
         } else {
            double var22 = var5 - var11;
            double var24 = var7 - var13;
            double var26 = var9 - var15;
            int var28 = Mth.sign(var22);
            int var29 = Mth.sign(var24);
            int var30 = Mth.sign(var26);
            double var31 = var28 == 0 ? 1.7976931348623157E308 : (double)var28 / var22;
            double var33 = var29 == 0 ? 1.7976931348623157E308 : (double)var29 / var24;
            double var35 = var30 == 0 ? 1.7976931348623157E308 : (double)var30 / var26;
            double var37 = var31 * (var28 > 0 ? 1.0 - Mth.frac(var11) : Mth.frac(var11));
            double var39 = var33 * (var29 > 0 ? 1.0 - Mth.frac(var13) : Mth.frac(var13));
            double var41 = var35 * (var30 > 0 ? 1.0 - Mth.frac(var15) : Mth.frac(var15));

            while(var37 <= 1.0 || var39 <= 1.0 || var41 <= 1.0) {
               if (var37 < var39) {
                  if (var37 < var41) {
                     var17 += var28;
                     var37 += var31;
                  } else {
                     var19 += var30;
                     var41 += var35;
                  }
               } else if (var39 < var41) {
                  var18 += var29;
                  var39 += var33;
               } else {
                  var19 += var30;
                  var41 += var35;
               }

               Object var43 = var3.apply(var2, var20.set(var17, var18, var19));
               if (var43 != null) {
                  return (T)var43;
               }
            }

            return (T)var4.apply(var2);
         }
      }
   }

   static boolean forEachBlockIntersectedBetween(Vec3 var0, Vec3 var1, AABB var2, BlockStepVisitor var3) {
      Vec3 var4 = var1.subtract(var0);
      if (var4.lengthSqr() < (double)Mth.square(1.0E-5F)) {
         for(BlockPos var11 : BlockPos.betweenClosed(var2)) {
            if (!var3.visit(var11, 0)) {
               return false;
            }
         }

         return true;
      } else {
         LongOpenHashSet var5 = new LongOpenHashSet();

         for(BlockPos var7 : BlockPos.betweenCornersInDirection(var2.move(var4.scale(-1.0)), var4)) {
            if (!var3.visit(var7, 0)) {
               return false;
            }

            var5.add(var7.asLong());
         }

         int var10 = addCollisionsAlongTravel(var5, var4, var2, var3);
         if (var10 < 0) {
            return false;
         } else {
            for(BlockPos var8 : BlockPos.betweenCornersInDirection(var2, var4)) {
               if (var5.add(var8.asLong()) && !var3.visit(var8, var10 + 1)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static int addCollisionsAlongTravel(LongSet var0, Vec3 var1, AABB var2, BlockStepVisitor var3) {
      double var4 = var2.getXsize();
      double var6 = var2.getYsize();
      double var8 = var2.getZsize();
      Vec3i var10 = getFurthestCorner(var1);
      Vec3 var11 = var2.getCenter();
      Vec3 var12 = new Vec3(var11.x() + var4 * 0.5 * (double)var10.getX(), var11.y() + var6 * 0.5 * (double)var10.getY(), var11.z() + var8 * 0.5 * (double)var10.getZ());
      Vec3 var13 = var12.subtract(var1);
      int var14 = Mth.floor(var13.x);
      int var15 = Mth.floor(var13.y);
      int var16 = Mth.floor(var13.z);
      int var17 = Mth.sign(var1.x);
      int var18 = Mth.sign(var1.y);
      int var19 = Mth.sign(var1.z);
      double var20 = var17 == 0 ? 1.7976931348623157E308 : (double)var17 / var1.x;
      double var22 = var18 == 0 ? 1.7976931348623157E308 : (double)var18 / var1.y;
      double var24 = var19 == 0 ? 1.7976931348623157E308 : (double)var19 / var1.z;
      double var26 = var20 * (var17 > 0 ? 1.0 - Mth.frac(var13.x) : Mth.frac(var13.x));
      double var28 = var22 * (var18 > 0 ? 1.0 - Mth.frac(var13.y) : Mth.frac(var13.y));
      double var30 = var24 * (var19 > 0 ? 1.0 - Mth.frac(var13.z) : Mth.frac(var13.z));
      int var32 = 0;

      while(var26 <= 1.0 || var28 <= 1.0 || var30 <= 1.0) {
         if (var26 < var28) {
            if (var26 < var30) {
               var14 += var17;
               var26 += var20;
            } else {
               var16 += var19;
               var30 += var24;
            }
         } else if (var28 < var30) {
            var15 += var18;
            var28 += var22;
         } else {
            var16 += var19;
            var30 += var24;
         }

         Optional var33 = AABB.clip((double)var14, (double)var15, (double)var16, (double)(var14 + 1), (double)(var15 + 1), (double)(var16 + 1), var13, var12);
         if (!var33.isEmpty()) {
            ++var32;
            Vec3 var34 = (Vec3)var33.get();
            double var35 = Mth.clamp(var34.x, (double)var14 + 9.999999747378752E-6, (double)var14 + 1.0 - 9.999999747378752E-6);
            double var37 = Mth.clamp(var34.y, (double)var15 + 9.999999747378752E-6, (double)var15 + 1.0 - 9.999999747378752E-6);
            double var39 = Mth.clamp(var34.z, (double)var16 + 9.999999747378752E-6, (double)var16 + 1.0 - 9.999999747378752E-6);
            int var41 = Mth.floor(var35 - var4 * (double)var10.getX());
            int var42 = Mth.floor(var37 - var6 * (double)var10.getY());
            int var43 = Mth.floor(var39 - var8 * (double)var10.getZ());
            int var44 = var32;

            for(BlockPos var46 : BlockPos.betweenCornersInDirection(var14, var15, var16, var41, var42, var43, var1)) {
               if (var0.add(var46.asLong()) && !var3.visit(var46, var44)) {
                  return -1;
               }
            }
         }
      }

      return var32;
   }

   private static Vec3i getFurthestCorner(Vec3 var0) {
      double var1 = Math.abs(Vec3.X_AXIS.dot(var0));
      double var3 = Math.abs(Vec3.Y_AXIS.dot(var0));
      double var5 = Math.abs(Vec3.Z_AXIS.dot(var0));
      int var7 = var0.x >= 0.0 ? 1 : -1;
      int var8 = var0.y >= 0.0 ? 1 : -1;
      int var9 = var0.z >= 0.0 ? 1 : -1;
      if (var1 <= var3 && var1 <= var5) {
         return new Vec3i(-var7, -var9, var8);
      } else {
         return var3 <= var5 ? new Vec3i(var9, -var8, -var7) : new Vec3i(-var8, var7, -var9);
      }
   }

   @FunctionalInterface
   public interface BlockStepVisitor {
      boolean visit(BlockPos var1, int var2);
   }
}
