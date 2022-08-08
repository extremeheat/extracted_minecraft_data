package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeBrownMushroomFeature extends AbstractHugeMushroomFeature {
   public HugeBrownMushroomFeature(Codec<HugeMushroomFeatureConfiguration> var1) {
      super(var1);
   }

   protected void makeCap(LevelAccessor var1, RandomSource var2, BlockPos var3, int var4, BlockPos.MutableBlockPos var5, HugeMushroomFeatureConfiguration var6) {
      int var7 = var6.foliageRadius;

      for(int var8 = -var7; var8 <= var7; ++var8) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            boolean var10 = var8 == -var7;
            boolean var11 = var8 == var7;
            boolean var12 = var9 == -var7;
            boolean var13 = var9 == var7;
            boolean var14 = var10 || var11;
            boolean var15 = var12 || var13;
            if (!var14 || !var15) {
               var5.setWithOffset(var3, var8, var4, var9);
               if (!var1.getBlockState(var5).isSolidRender(var1, var5)) {
                  boolean var16 = var10 || var15 && var8 == 1 - var7;
                  boolean var17 = var11 || var15 && var8 == var7 - 1;
                  boolean var18 = var12 || var14 && var9 == 1 - var7;
                  boolean var19 = var13 || var14 && var9 == var7 - 1;
                  BlockState var20 = var6.capProvider.getState(var2, var3);
                  if (var20.hasProperty(HugeMushroomBlock.WEST) && var20.hasProperty(HugeMushroomBlock.EAST) && var20.hasProperty(HugeMushroomBlock.NORTH) && var20.hasProperty(HugeMushroomBlock.SOUTH)) {
                     var20 = (BlockState)((BlockState)((BlockState)((BlockState)var20.setValue(HugeMushroomBlock.WEST, var16)).setValue(HugeMushroomBlock.EAST, var17)).setValue(HugeMushroomBlock.NORTH, var18)).setValue(HugeMushroomBlock.SOUTH, var19);
                  }

                  this.setBlock(var1, var5, var20);
               }
            }
         }
      }

   }

   protected int getTreeRadiusForHeight(int var1, int var2, int var3, int var4) {
      return var4 <= 3 ? 0 : var3;
   }
}
