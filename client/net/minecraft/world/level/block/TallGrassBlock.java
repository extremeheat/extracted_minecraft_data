package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallGrassBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected TallGrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      DoublePlantBlock var5 = (DoublePlantBlock)((DoublePlantBlock)(var4.is(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS));
      if (var5.defaultBlockState().canSurvive(var1, var3) && var1.isEmptyBlock(var3.above())) {
         var5.placeAt(var1, var3, 2);
      }

   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XYZ;
   }
}
