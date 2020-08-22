package net.minecraft.world.level;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter {
   @Nullable
   BlockEntity getBlockEntity(BlockPos var1);

   BlockState getBlockState(BlockPos var1);

   FluidState getFluidState(BlockPos var1);

   default int getLightEmission(BlockPos var1) {
      return this.getBlockState(var1).getLightEmission();
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default int getMaxBuildHeight() {
      return 256;
   }

   default BlockHitResult clip(ClipContext var1) {
      return (BlockHitResult)traverseBlocks(var1, (var1x, var2) -> {
         BlockState var3 = this.getBlockState(var2);
         FluidState var4 = this.getFluidState(var2);
         Vec3 var5 = var1x.getFrom();
         Vec3 var6 = var1x.getTo();
         VoxelShape var7 = var1x.getBlockShape(var3, this, var2);
         BlockHitResult var8 = this.clipWithInteractionOverride(var5, var6, var2, var7, var3);
         VoxelShape var9 = var1x.getFluidShape(var4, this, var2);
         BlockHitResult var10 = var9.clip(var5, var6, var2);
         double var11 = var8 == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(var8.getLocation());
         double var13 = var10 == null ? Double.MAX_VALUE : var1x.getFrom().distanceToSqr(var10.getLocation());
         return var11 <= var13 ? var8 : var10;
      }, (var0) -> {
         Vec3 var1 = var0.getFrom().subtract(var0.getTo());
         return BlockHitResult.miss(var0.getTo(), Direction.getNearest(var1.x, var1.y, var1.z), new BlockPos(var0.getTo()));
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

   static Object traverseBlocks(ClipContext var0, BiFunction var1, Function var2) {
      Vec3 var3 = var0.getFrom();
      Vec3 var4 = var0.getTo();
      if (var3.equals(var4)) {
         return var2.apply(var0);
      } else {
         double var5 = Mth.lerp(-1.0E-7D, var4.x, var3.x);
         double var7 = Mth.lerp(-1.0E-7D, var4.y, var3.y);
         double var9 = Mth.lerp(-1.0E-7D, var4.z, var3.z);
         double var11 = Mth.lerp(-1.0E-7D, var3.x, var4.x);
         double var13 = Mth.lerp(-1.0E-7D, var3.y, var4.y);
         double var15 = Mth.lerp(-1.0E-7D, var3.z, var4.z);
         int var17 = Mth.floor(var11);
         int var18 = Mth.floor(var13);
         int var19 = Mth.floor(var15);
         BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos(var17, var18, var19);
         Object var21 = var1.apply(var0, var20);
         if (var21 != null) {
            return var21;
         } else {
            double var22 = var5 - var11;
            double var24 = var7 - var13;
            double var26 = var9 - var15;
            int var28 = Mth.sign(var22);
            int var29 = Mth.sign(var24);
            int var30 = Mth.sign(var26);
            double var31 = var28 == 0 ? Double.MAX_VALUE : (double)var28 / var22;
            double var33 = var29 == 0 ? Double.MAX_VALUE : (double)var29 / var24;
            double var35 = var30 == 0 ? Double.MAX_VALUE : (double)var30 / var26;
            double var37 = var31 * (var28 > 0 ? 1.0D - Mth.frac(var11) : Mth.frac(var11));
            double var39 = var33 * (var29 > 0 ? 1.0D - Mth.frac(var13) : Mth.frac(var13));
            double var41 = var35 * (var30 > 0 ? 1.0D - Mth.frac(var15) : Mth.frac(var15));

            Object var43;
            do {
               if (var37 > 1.0D && var39 > 1.0D && var41 > 1.0D) {
                  return var2.apply(var0);
               }

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

               var43 = var1.apply(var0, var20.set(var17, var18, var19));
            } while(var43 == null);

            return var43;
         }
      }
   }
}
