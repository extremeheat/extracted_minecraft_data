package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class HorizontalDirectionalBlock extends Block {
   public static final EnumProperty<Direction> FACING;

   protected HorizontalDirectionalBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected abstract MapCodec<? extends HorizontalDirectionalBlock> codec();

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
   }
}
