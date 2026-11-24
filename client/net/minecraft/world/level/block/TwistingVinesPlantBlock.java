package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.FellFromLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesPlantBlock extends GrowingPlantBodyBlock {
   private static final VoxelShape SHAPE = Block.column(8.0, 0.0, 16.0);

   public TwistingVinesPlantBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.UP, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.TWISTING_VINES;
   }

   protected FellFromLocation getFellFromLocation(final BlockState state, final Level level, final BlockPos pos, final LivingEntity mob) {
      return FellFromLocation.TWISTING_VINES;
   }
}
