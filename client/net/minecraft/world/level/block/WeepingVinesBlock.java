package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingVinesBlock extends GrowingPlantHeadBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public WeepingVinesBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.DOWN, SHAPE, false, 0.1D);
   }

   protected int getBlocksToGrowWhenBonemealed(Random var1) {
      return NetherVines.getBlocksToGrowWhenBonemealed(var1);
   }

   protected Block getBodyBlock() {
      return Blocks.WEEPING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState var1) {
      return NetherVines.isValidGrowthState(var1);
   }
}
