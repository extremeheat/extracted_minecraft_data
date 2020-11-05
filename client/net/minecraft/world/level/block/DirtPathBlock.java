package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirtPathBlock extends Block {
   protected static final VoxelShape SHAPE;

   protected DirtPathBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return !this.defaultBlockState().canSurvive(var1.getLevel(), var1.getClickedPos()) ? Block.pushEntitiesUp(this.defaultBlockState(), Blocks.DIRT.defaultBlockState(), var1.getLevel(), var1.getClickedPos()) : super.getStateForPlacement(var1);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && !var1.canSurvive(var4, var5)) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      FarmBlock.turnToDirt(var1, var2, var3);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.above());
      return !var4.getMaterial().isSolid() || var4.getBlock() instanceof FenceGateBlock;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      SHAPE = FarmBlock.SHAPE;
   }
}
