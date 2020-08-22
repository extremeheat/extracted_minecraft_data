package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class GroundBushFeature extends AbstractTreeFeature {
   public GroundBushFeature(Function var1) {
      super(var1);
   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, TreeConfiguration var7) {
      var3 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var3).below();
      if (isGrassOrDirt(var1, var3)) {
         var3 = var3.above();
         this.placeLog(var1, var2, var3, var4, var6, var7);

         for(int var8 = 0; var8 <= 2; ++var8) {
            int var9 = 2 - var8;

            for(int var10 = -var9; var10 <= var9; ++var10) {
               for(int var11 = -var9; var11 <= var9; ++var11) {
                  if (Math.abs(var10) != var9 || Math.abs(var11) != var9 || var2.nextInt(2) != 0) {
                     this.placeLeaf(var1, var2, new BlockPos(var10 + var3.getX(), var8 + var3.getY(), var11 + var3.getZ()), var5, var6, var7);
                  }
               }
            }
         }
      }

      return true;
   }
}
