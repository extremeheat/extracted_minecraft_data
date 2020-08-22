package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class TreeFeature extends AbstractSmallTreeFeature {
   public TreeFeature(Function var1) {
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
         var7.foliagePlacer.createFoliage(var1, var2, var7, var8, var9, var10, var12, var5);
         this.placeTrunk(var1, var2, var8, var12, var7.trunkTopOffset + var2.nextInt(var7.trunkTopOffsetRandom + 1), var4, var6, var7);
         return true;
      }
   }
}
