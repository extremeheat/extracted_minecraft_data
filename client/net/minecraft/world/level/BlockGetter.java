package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter extends LevelHeightAccessor {
   int MAX_BLOCK_ITERATIONS_ALONG_TRAVEL = 16;

   @Nullable
   BlockEntity getBlockEntity(BlockPos var1);

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

   @Nullable
   default BlockHitResult clipWithInteractionOverride(Vec3 var1, Vec3 var2, BlockPos var3, VoxelShape var4, BlockState var5) {
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

   static <T, C> T traverseBlocks(Vec3 var0, Vec3 var1, C var2, BiFunction<C, BlockPos, T> var3, Function<C, T> var4) {
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
      if (var4.lengthSqr() < (double)Mth.square(0.99999F)) {
         for(BlockPos var12 : BlockPos.betweenClosed(var2)) {
            if (!var3.visit(var12, 0)) {
               return false;
            }
         }

         return true;
      } else {
         LongOpenHashSet var5 = new LongOpenHashSet();
         Vec3 var6 = var2.getMinPosition();
         Vec3 var7 = var6.subtract(var4);
         int var8 = addCollisionsAlongTravel(var5, var7, var6, var2, var3);
         if (var8 < 0) {
            return false;
         } else {
            for(BlockPos var10 : BlockPos.betweenClosed(var2)) {
               if (!var5.contains(var10.asLong()) && !var3.visit(var10, var8 + 1)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static int addCollisionsAlongTravel(LongSet var0, Vec3 var1, Vec3 var2, AABB var3, BlockStepVisitor var4) {
      Vec3 var5 = var2.subtract(var1);
      int var6 = Mth.floor(var1.x);
      int var7 = Mth.floor(var1.y);
      int var8 = Mth.floor(var1.z);
      int var9 = Mth.sign(var5.x);
      int var10 = Mth.sign(var5.y);
      int var11 = Mth.sign(var5.z);
      double var12 = var9 == 0 ? 1.7976931348623157E308 : (double)var9 / var5.x;
      double var14 = var10 == 0 ? 1.7976931348623157E308 : (double)var10 / var5.y;
      double var16 = var11 == 0 ? 1.7976931348623157E308 : (double)var11 / var5.z;
      double var18 = var12 * (var9 > 0 ? 1.0 - Mth.frac(var1.x) : Mth.frac(var1.x));
      double var20 = var14 * (var10 > 0 ? 1.0 - Mth.frac(var1.y) : Mth.frac(var1.y));
      double var22 = var16 * (var11 > 0 ? 1.0 - Mth.frac(var1.z) : Mth.frac(var1.z));
      int var24 = 0;
      BlockPos.MutableBlockPos var25 = new BlockPos.MutableBlockPos();

      while(var18 <= 1.0 || var20 <= 1.0 || var22 <= 1.0) {
         if (var18 < var20) {
            if (var18 < var22) {
               var6 += var9;
               var18 += var12;
            } else {
               var8 += var11;
               var22 += var16;
            }
         } else if (var20 < var22) {
            var7 += var10;
            var20 += var14;
         } else {
            var8 += var11;
            var22 += var16;
         }

         if (var24++ > 16) {
            break;
         }

         Optional var26 = AABB.clip((double)var6, (double)var7, (double)var8, (double)(var6 + 1), (double)(var7 + 1), (double)(var8 + 1), var1, var2);
         if (!var26.isEmpty()) {
            Vec3 var27 = (Vec3)var26.get();
            double var28 = Mth.clamp(var27.x, (double)var6 + 9.999999747378752E-6, (double)var6 + 1.0 - 9.999999747378752E-6);
            double var30 = Mth.clamp(var27.y, (double)var7 + 9.999999747378752E-6, (double)var7 + 1.0 - 9.999999747378752E-6);
            double var32 = Mth.clamp(var27.z, (double)var8 + 9.999999747378752E-6, (double)var8 + 1.0 - 9.999999747378752E-6);
            int var34 = Mth.floor(var28 + var3.getXsize());
            int var35 = Mth.floor(var30 + var3.getYsize());
            int var36 = Mth.floor(var32 + var3.getZsize());

            for(int var37 = var6; var37 <= var34; ++var37) {
               for(int var38 = var7; var38 <= var35; ++var38) {
                  for(int var39 = var8; var39 <= var36; ++var39) {
                     if (var0.add(BlockPos.asLong(var37, var38, var39)) && !var4.visit(var25.set(var37, var38, var39), var24)) {
                        return -1;
                     }
                  }
               }
            }
         }
      }

      return var24;
   }

   @FunctionalInterface
   public interface BlockStepVisitor {
      boolean visit(BlockPos var1, int var2);
   }
}
