package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AcaciaFeature extends AbstractSmallTreeFeature {
   public AcaciaFeature(Function var1) {
      super(var1);
   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, SmallTreeConfiguration var7) {
      int var8 = var7.baseHeight + var2.nextInt(var7.heightRandA + 1) + var2.nextInt(var7.heightRandB + 1);
      int var9 = var7.trunkHeight >= 0 ? var7.trunkHeight + var2.nextInt(var7.trunkHeightRandom + 1) : var8 - (var7.foliageHeight + var2.nextInt(var7.foliageHeightRandom + 1));
      int var10 = var7.foliagePlacer.foliageRadius(var2, var9, var8, var7);
      Optional var11 = this.getProjectedOrigin(var1, var8, var9, var10, var3, var7);
      if (!var11.isPresent()) {
         return false;
      } else {
         BlockPos var12 = (BlockPos)var11.get();
         this.setDirtAt(var1, var12.below());
         Direction var13 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
         int var14 = var8 - var2.nextInt(4) - 1;
         int var15 = 3 - var2.nextInt(3);
         BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();
         int var17 = var12.getX();
         int var18 = var12.getZ();
         int var19 = 0;

         int var21;
         for(int var20 = 0; var20 < var8; ++var20) {
            var21 = var12.getY() + var20;
            if (var20 >= var14 && var15 > 0) {
               var17 += var13.getStepX();
               var18 += var13.getStepZ();
               --var15;
            }

            if (this.placeLog(var1, var2, var16.set(var17, var21, var18), var4, var6, var7)) {
               var19 = var21;
            }
         }

         BlockPos var25 = new BlockPos(var17, var19, var18);
         var7.foliagePlacer.createFoliage(var1, var2, var7, var8, var9, var10 + 1, var25, var5);
         var17 = var12.getX();
         var18 = var12.getZ();
         Direction var26 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
         if (var26 != var13) {
            var21 = var14 - var2.nextInt(2) - 1;
            int var22 = 1 + var2.nextInt(3);
            var19 = 0;

            for(int var23 = var21; var23 < var8 && var22 > 0; --var22) {
               if (var23 >= 1) {
                  int var24 = var12.getY() + var23;
                  var17 += var26.getStepX();
                  var18 += var26.getStepZ();
                  if (this.placeLog(var1, var2, var16.set(var17, var24, var18), var4, var6, var7)) {
                     var19 = var24;
                  }
               }

               ++var23;
            }

            if (var19 > 0) {
               BlockPos var27 = new BlockPos(var17, var19, var18);
               var7.foliagePlacer.createFoliage(var1, var2, var7, var8, var9, var10, var27, var5);
            }
         }

         return true;
      }
   }
}
