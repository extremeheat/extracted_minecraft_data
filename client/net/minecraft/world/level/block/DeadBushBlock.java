package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends BushBlock {
   public static final MapCodec<DeadBushBlock> CODEC = simpleCodec(DeadBushBlock::new);
   private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 13.0);

   public MapCodec<DeadBushBlock> codec() {
      return CODEC;
   }

   protected DeadBushBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.DEAD_BUSH_MAY_PLACE_ON);
   }
}
