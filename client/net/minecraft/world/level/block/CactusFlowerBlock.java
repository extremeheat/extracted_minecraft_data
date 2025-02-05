package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusFlowerBlock extends VegetationBlock {
   public static final MapCodec<CactusFlowerBlock> CODEC = simpleCodec(CactusFlowerBlock::new);
   private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 12.0);

   public MapCodec<? extends CactusFlowerBlock> codec() {
      return CODEC;
   }

   public CactusFlowerBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3);
      return var4.is(Blocks.CACTUS) || var4.isFaceSturdy(var2, var3, Direction.UP, SupportType.CENTER);
   }
}
