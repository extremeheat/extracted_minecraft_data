package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeRedMushroomFeature extends AbstractHugeMushroomFeature {
   public HugeRedMushroomFeature(Codec<HugeMushroomFeatureConfiguration> var1) {
      super(var1);
   }

   protected void makeCap(LevelAccessor var1, RandomSource var2, BlockPos var3, int var4, BlockPos.MutableBlockPos var5, HugeMushroomFeatureConfiguration var6) {
      for(int var7 = var4 - 3; var7 <= var4; ++var7) {
         int var8 = var7 < var4 ? var6.foliageRadius : var6.foliageRadius - 1;
         int var9 = var6.foliageRadius - 2;

         for(int var10 = -var8; var10 <= var8; ++var10) {
            for(int var11 = -var8; var11 <= var8; ++var11) {
               boolean var12 = var10 == -var8;
               boolean var13 = var10 == var8;
               boolean var14 = var11 == -var8;
               boolean var15 = var11 == var8;
               boolean var16 = var12 || var13;
               boolean var17 = var14 || var15;
               if (var7 >= var4 || var16 != var17) {
                  var5.setWithOffset(var3, var10, var7, var11);
                  if (!var1.getBlockState(var5).isSolidRender(var1, var5)) {
                     BlockState var18 = var6.capProvider.getState(var2, var3);
                     if (var18.hasProperty(HugeMushroomBlock.WEST) && var18.hasProperty(HugeMushroomBlock.EAST) && var18.hasProperty(HugeMushroomBlock.NORTH) && var18.hasProperty(HugeMushroomBlock.SOUTH) && var18.hasProperty(HugeMushroomBlock.UP)) {
                        var18 = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var18.setValue(HugeMushroomBlock.UP, var7 >= var4 - 1)).setValue(HugeMushroomBlock.WEST, var10 < -var9)).setValue(HugeMushroomBlock.EAST, var10 > var9)).setValue(HugeMushroomBlock.NORTH, var11 < -var9)).setValue(HugeMushroomBlock.SOUTH, var11 > var9);
                     }

                     this.setBlock(var1, var5, var18);
                  }
               }
            }
         }
      }

   }

   protected int getTreeRadiusForHeight(int var1, int var2, int var3, int var4) {
      int var5 = 0;
      if (var4 < var2 && var4 >= var2 - 3) {
         var5 = var3;
      } else if (var4 == var2) {
         var5 = var3;
      }

      return var5;
   }
}
