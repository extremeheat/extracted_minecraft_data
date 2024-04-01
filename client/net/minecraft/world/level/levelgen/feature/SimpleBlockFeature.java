package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature extends Feature<SimpleBlockConfiguration> {
   public SimpleBlockFeature(Codec<SimpleBlockConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> var1) {
      SimpleBlockConfiguration var2 = (SimpleBlockConfiguration)var1.config();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      BlockState var5 = var2.toPlace().getState(var1.random(), var4);
      if (var5.is(Blocks.POTATOES)) {
         var5 = PotatoBlock.withCorrectTaterBoost(var5, var3.getBlockState(var4.below()));
      }

      return var5.canSurvive(var3, var4) ? place(var5, var3, var4) : false;
   }

   public static boolean place(BlockState var0, WorldGenLevel var1, BlockPos var2) {
      if (var0.getBlock() instanceof DoublePlantBlock) {
         if (!var1.isEmptyBlock(var2.above())) {
            return false;
         }

         DoublePlantBlock.placeAt(var1, var0, var2, 2);
      } else {
         var1.setBlock(var2, var0, 2);
      }

      return true;
   }
}
