package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class DirectionalBlock extends Block {
   public static final DirectionProperty FACING;

   protected DirectionalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   static {
      FACING = BlockStateProperties.FACING;
   }
}
