package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Optional;
import java.util.Set;
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
   @Nullable
   BlockEntity getBlockEntity(BlockPos var1);

   default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos var1, BlockEntityType<T> var2) {
      BlockEntity var3 = this.getBlockEntity(var1);
      return var3 != null && var3.getType() == var2 ? Optional.of((T)var3) : Optional.empty();
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
      return traverseBlocks(
         var1.getFrom(),
         var1.getTo(),
         var1,
         (var1x, var2) -> {
            BlockState var3 = this.getBlockState(var2);
            Vec3 var4 = var1x.getFrom().subtract(var1x.getTo());
            return var1x.isTargetBlock().test(var3)
               ? new BlockHitResult(var1x.getTo(), Direction.getApproximateNearest(var4.x, var4.y, var4.z), BlockPos.containing(var1x.getTo()), false)
               : null;
         },
         var0 -> {
            Vec3 var1x = var0.getFrom().subtract(var0.getTo());
            return BlockHitResult.miss(var0.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0.getTo()));
         }
      );
   }

   default BlockHitResult clip(ClipContext var1) {
      return traverseBlocks(var1.getFrom(), var1.getTo(), var1, (var1x, var2) -> {
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
      }, var0 -> {
         Vec3 var1x = var0.getFrom().subtract(var0.getTo());
         return BlockHitResult.miss(var0.getTo(), Direction.getApproximateNearest(var1x.x, var1x.y, var1x.z), BlockPos.containing(var0.getTo()));
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

            while (var37 <= 1.0 || var39 <= 1.0 || var41 <= 1.0) {
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

   static Iterable<BlockPos> boxTraverseBlocks(Vec3 var0, Vec3 var1, AABB var2) {
      AABB var3 = var2.inflate(9.999999747378752E-6);
      Vec3 var4 = var1.subtract(var0);
      Iterable var5 = BlockPos.betweenClosed(var3);
      if (var4.lengthSqr() < (double)Mth.square(0.99999F)) {
         return var5;
      } else {
         ObjectOpenHashSet var6 = new ObjectOpenHashSet();

         for (BlockPos var8 : var5) {
            var6.add(var8.immutable());
         }

         Vec3 var10 = var4.normalize().scale(1.0E-7);
         Vec3 var11 = var2.getMinPosition().add(var10);
         Vec3 var9 = var2.getMinPosition().subtract(var4).subtract(var10);
         addCollisionsAlongTravel(var6, var9, var11, var3);
         return var6;
      }
   }

   private static void addCollisionsAlongTravel(Set<BlockPos> var0, Vec3 var1, Vec3 var2, AABB var3) {
      Vec3 var4 = var2.subtract(var1);
      int var5 = Mth.floor(var1.x);
      int var6 = Mth.floor(var1.y);
      int var7 = Mth.floor(var1.z);
      int var8 = Mth.sign(var4.x);
      int var9 = Mth.sign(var4.y);
      int var10 = Mth.sign(var4.z);
      double var11 = var8 == 0 ? 1.7976931348623157E308 : (double)var8 / var4.x;
      double var13 = var9 == 0 ? 1.7976931348623157E308 : (double)var9 / var4.y;
      double var15 = var10 == 0 ? 1.7976931348623157E308 : (double)var10 / var4.z;
      double var17 = var11 * (var8 > 0 ? 1.0 - Mth.frac(var1.x) : Mth.frac(var1.x));
      double var19 = var13 * (var9 > 0 ? 1.0 - Mth.frac(var1.y) : Mth.frac(var1.y));
      double var21 = var15 * (var10 > 0 ? 1.0 - Mth.frac(var1.z) : Mth.frac(var1.z));

      while (var17 <= 1.0 || var19 <= 1.0 || var21 <= 1.0) {
         if (var17 < var19) {
            if (var17 < var21) {
               var5 += var8;
               var17 += var11;
            } else {
               var7 += var10;
               var21 += var15;
            }
         } else if (var19 < var21) {
            var6 += var9;
            var19 += var13;
         } else {
            var7 += var10;
            var21 += var15;
         }

         Optional var23 = AABB.clip((double)var5, (double)var6, (double)var7, (double)(var5 + 1), (double)(var6 + 1), (double)(var7 + 1), var1, var2);
         if (!var23.isEmpty()) {
            Vec3 var24 = (Vec3)var23.get();
            double var25 = Mth.clamp(var24.x, (double)var5 + 9.999999747378752E-6, (double)var5 + 1.0 - 9.999999747378752E-6);
            double var27 = Mth.clamp(var24.y, (double)var6 + 9.999999747378752E-6, (double)var6 + 1.0 - 9.999999747378752E-6);
            double var29 = Mth.clamp(var24.z, (double)var7 + 9.999999747378752E-6, (double)var7 + 1.0 - 9.999999747378752E-6);
            int var31 = Mth.floor(var25 + var3.getXsize());
            int var32 = Mth.floor(var27 + var3.getYsize());
            int var33 = Mth.floor(var29 + var3.getZsize());

            for (int var34 = var5; var34 <= var31; var34++) {
               for (int var35 = var6; var35 <= var32; var35++) {
                  for (int var36 = var7; var36 <= var33; var36++) {
                     var0.add(new BlockPos(var34, var35, var36));
                  }
               }
            }
         }
      }
   }
}
