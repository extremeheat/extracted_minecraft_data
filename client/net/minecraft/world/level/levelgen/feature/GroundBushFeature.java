package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class GroundBushFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private final BlockState leaf;
   private final BlockState trunk;

   public GroundBushFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, BlockState var2, BlockState var3) {
      super(var1, false);
      this.trunk = var2;
      this.leaf = var3;
   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      var4 = var2.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var4).below();
      if (isGrassOrDirt(var2, var4)) {
         var4 = var4.above();
         this.setBlock(var1, var2, var4, this.trunk, var5);

         for(int var6 = var4.getY(); var6 <= var4.getY() + 2; ++var6) {
            int var7 = var6 - var4.getY();
            int var8 = 2 - var7;

            for(int var9 = var4.getX() - var8; var9 <= var4.getX() + var8; ++var9) {
               int var10 = var9 - var4.getX();

               for(int var11 = var4.getZ() - var8; var11 <= var4.getZ() + var8; ++var11) {
                  int var12 = var11 - var4.getZ();
                  if (Math.abs(var10) != var8 || Math.abs(var12) != var8 || var3.nextInt(2) != 0) {
                     BlockPos var13 = new BlockPos(var9, var6, var11);
                     if (isAirOrLeaves(var2, var13)) {
                        this.setBlock(var1, var2, var13, this.leaf, var5);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
