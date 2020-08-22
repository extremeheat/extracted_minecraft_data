package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirBlock extends Block {
   protected AirBlock(Block.Properties var1) {
      super(var1);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public boolean isAir(BlockState var1) {
      return true;
   }
}
