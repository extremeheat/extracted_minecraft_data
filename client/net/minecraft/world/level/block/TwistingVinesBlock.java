package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesBlock extends GrowingPlantHeadBlock {
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

   public TwistingVinesBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.UP, SHAPE, false, 0.1D);
   }

   protected int getBlocksToGrowWhenBonemealed(Random var1) {
      return NetherVines.getBlocksToGrowWhenBonemealed(var1);
   }

   protected Block getBodyBlock() {
      return Blocks.TWISTING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState var1) {
      return NetherVines.isValidGrowthState(var1);
   }
}
