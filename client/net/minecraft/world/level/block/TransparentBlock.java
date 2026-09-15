package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransparentBlock extends HalfTransparentBlock {
   protected TransparentBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected VoxelShape getVisualShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return Shapes.empty();
   }

   protected float getShadeBrightness(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return 1.0F;
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return true;
   }
}
