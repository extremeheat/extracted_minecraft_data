package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HayBlockPileFeature extends BlockPileFeature {
   public HayBlockPileFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected BlockState getBlockState(LevelAccessor var1) {
      Direction.Axis var2 = Direction.Axis.getRandomAxis(var1.getRandom());
      return (BlockState)Blocks.HAY_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var2);
   }
}
