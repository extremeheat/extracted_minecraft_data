package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class KelpFeature extends Feature<NoneFeatureConfiguration> {
   public KelpFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      int var2 = 0;
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      RandomSource var5 = var1.random();
      int var6 = var3.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX(), var4.getZ());
      BlockPos var7 = new BlockPos(var4.getX(), var6, var4.getZ());
      if (var3.getBlockState(var7).is(Blocks.WATER)) {
         BlockState var8 = Blocks.KELP.defaultBlockState();
         BlockState var9 = Blocks.KELP_PLANT.defaultBlockState();
         int var10 = 1 + var5.nextInt(10);

         for(int var11 = 0; var11 <= var10; ++var11) {
            if (var3.getBlockState(var7).is(Blocks.WATER) && var3.getBlockState(var7.above()).is(Blocks.WATER) && var9.canSurvive(var3, var7)) {
               if (var11 == var10) {
                  var3.setBlock(var7, (BlockState)var8.setValue(KelpBlock.AGE, var5.nextInt(4) + 20), 2);
                  ++var2;
               } else {
                  var3.setBlock(var7, var9, 2);
               }
            } else if (var11 > 0) {
               BlockPos var12 = var7.below();
               if (var8.canSurvive(var3, var12) && !var3.getBlockState(var12.below()).is(Blocks.KELP)) {
                  var3.setBlock(var12, (BlockState)var8.setValue(KelpBlock.AGE, var5.nextInt(4) + 20), 2);
                  ++var2;
               }
               break;
            }

            var7 = var7.above();
         }
      }

      return var2 > 0;
   }
}
