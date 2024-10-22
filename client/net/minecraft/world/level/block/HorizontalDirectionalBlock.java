package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class HorizontalDirectionalBlock extends Block {
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected HorizontalDirectionalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected abstract MapCodec<? extends HorizontalDirectionalBlock> codec();

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }
}
