package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class DefaultFlowerFeature extends AbstractFlowerFeature<RandomPatchConfiguration> {
   public DefaultFlowerFeature(Codec<RandomPatchConfiguration> var1) {
      super(var1);
   }

   public boolean isValid(LevelAccessor var1, BlockPos var2, RandomPatchConfiguration var3) {
      return !var3.blacklist.contains(var1.getBlockState(var2));
   }

   public int getCount(RandomPatchConfiguration var1) {
      return var1.tries;
   }

   public BlockPos getPos(Random var1, BlockPos var2, RandomPatchConfiguration var3) {
      return var2.offset(var1.nextInt(var3.xspread) - var1.nextInt(var3.xspread), var1.nextInt(var3.yspread) - var1.nextInt(var3.yspread), var1.nextInt(var3.zspread) - var1.nextInt(var3.zspread));
   }

   public BlockState getRandomFlower(Random var1, BlockPos var2, RandomPatchConfiguration var3) {
      return var3.stateProvider.getState(var1, var2);
   }
}
