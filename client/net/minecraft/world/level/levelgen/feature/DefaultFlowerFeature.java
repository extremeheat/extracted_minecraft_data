package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultFlowerFeature extends FlowerFeature {
   public DefaultFlowerFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public BlockState getRandomFlower(Random var1, BlockPos var2) {
      return var1.nextFloat() > 0.6666667F ? Blocks.DANDELION.defaultBlockState() : Blocks.POPPY.defaultBlockState();
   }
}
