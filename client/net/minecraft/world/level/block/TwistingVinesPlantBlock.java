package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesPlantBlock extends GrowingPlantBodyBlock {
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public TwistingVinesPlantBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.UP, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.TWISTING_VINES;
   }
}
