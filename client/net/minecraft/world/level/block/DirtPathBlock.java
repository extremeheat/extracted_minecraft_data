package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirtPathBlock extends Block {
   public static final MapCodec<DirtPathBlock> CODEC = simpleCodec(DirtPathBlock::new);
   protected static final VoxelShape SHAPE = FarmBlock.SHAPE;

   @Override
   public MapCodec<DirtPathBlock> codec() {
      return CODEC;
   }

   protected DirtPathBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return !this.defaultBlockState().canSurvive(var1.getLevel(), var1.getClickedPos())
         ? Block.pushEntitiesUp(this.defaultBlockState(), Blocks.DIRT.defaultBlockState(), var1.getLevel(), var1.getClickedPos())
         : super.getStateForPlacement(var1);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var5 == Direction.UP && !var1.canSurvive(var2, var4)) {
         var3.scheduleTick(var4, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      FarmBlock.turnToDirt(null, var1, var2, var3);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.above());
      return !var4.isSolid() || var4.getBlock() instanceof FenceGateBlock;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
