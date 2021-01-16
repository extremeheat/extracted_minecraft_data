package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class GrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
   public GrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = Blocks.GRASS.defaultBlockState();

      label48:
      for(int var7 = 0; var7 < 128; ++var7) {
         BlockPos var8 = var5;

         for(int var9 = 0; var9 < var7 / 16; ++var9) {
            var8 = var8.offset(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (!var1.getBlockState(var8.below()).is(this) || var1.getBlockState(var8).isCollisionShapeFullBlock(var1, var8)) {
               continue label48;
            }
         }

         BlockState var14 = var1.getBlockState(var8);
         if (var14.is(var6.getBlock()) && var2.nextInt(10) == 0) {
            ((BonemealableBlock)var6.getBlock()).performBonemeal(var1, var2, var8, var14);
         }

         if (var14.isAir()) {
            BlockState var10;
            if (var2.nextInt(8) == 0) {
               List var11 = var1.getBiome(var8).getGenerationSettings().getFlowerFeatures();
               if (var11.isEmpty()) {
                  continue;
               }

               ConfiguredFeature var12 = (ConfiguredFeature)var11.get(0);
               AbstractFlowerFeature var13 = (AbstractFlowerFeature)var12.feature;
               var10 = var13.getRandomFlower(var2, var8, var12.config());
            } else {
               var10 = var6;
            }

            if (var10.canSurvive(var1, var8)) {
               var1.setBlock(var8, var10, 3);
            }
         }
      }

   }
}
