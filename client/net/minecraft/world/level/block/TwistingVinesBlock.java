package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesBlock extends GrowingPlantHeadBlock {
   public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

   public TwistingVinesBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.UP, SHAPE, false, 0.1);
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource var1) {
      return NetherVines.getBlocksToGrowWhenBonemealed(var1);
   }

   protected Block getBodyBlock() {
      return Blocks.TWISTING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState var1) {
      return NetherVines.isValidGrowthState(var1);
   }
}
