package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class AbstractSmallTreeFeature extends AbstractTreeFeature {
   public AbstractSmallTreeFeature(Function var1) {
      super(var1);
   }

   protected void placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, int var5, Set var6, BoundingBox var7, SmallTreeConfiguration var8) {
      for(int var9 = 0; var9 < var3 - var5; ++var9) {
         this.placeLog(var1, var2, var4.above(var9), var6, var7, var8);
      }

   }

   public Optional getProjectedOrigin(LevelSimulatedRW var1, int var2, int var3, int var4, BlockPos var5, SmallTreeConfiguration var6) {
      BlockPos var7;
      int var8;
      int var9;
      if (!var6.fromSapling) {
         var8 = var1.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, var5).getY();
         var9 = var1.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var5).getY();
         var7 = new BlockPos(var5.getX(), var8, var5.getZ());
         if (var9 - var8 > var6.maxWaterDepth) {
            return Optional.empty();
         }
      } else {
         var7 = var5;
      }

      if (var7.getY() >= 1 && var7.getY() + var2 + 1 <= 256) {
         for(var8 = 0; var8 <= var2 + 1; ++var8) {
            var9 = var6.foliagePlacer.getTreeRadiusForHeight(var3, var2, var4, var8);
            BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

            for(int var11 = -var9; var11 <= var9; ++var11) {
               int var12 = -var9;

               while(var12 <= var9) {
                  if (var8 + var7.getY() >= 0 && var8 + var7.getY() < 256) {
                     var10.set(var11 + var7.getX(), var8 + var7.getY(), var12 + var7.getZ());
                     if (isFree(var1, var10) && (var6.ignoreVines || !isVine(var1, var10))) {
                        ++var12;
                        continue;
                     }

                     return Optional.empty();
                  }

                  return Optional.empty();
               }
            }
         }

         if (isGrassOrDirtOrFarmland(var1, var7.below()) && var7.getY() < 256 - var2 - 1) {
            return Optional.of(var7);
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }
}
