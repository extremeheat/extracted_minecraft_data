package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CocoaBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
   public static final int MAX_AGE = 2;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
   protected static final int AGE_0_WIDTH = 4;
   protected static final int AGE_0_HEIGHT = 5;
   protected static final int AGE_0_HALFWIDTH = 2;
   protected static final int AGE_1_WIDTH = 6;
   protected static final int AGE_1_HEIGHT = 7;
   protected static final int AGE_1_HALFWIDTH = 3;
   protected static final int AGE_2_WIDTH = 8;
   protected static final int AGE_2_HEIGHT = 9;
   protected static final int AGE_2_HALFWIDTH = 4;
   protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{
      Block.box(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.box(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.box(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
   };
   protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{
      Block.box(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.box(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.box(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
   };
   protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{
      Block.box(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.box(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.box(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
   };
   protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{
      Block.box(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.box(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.box(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
   };

   public CocoaBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   public boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(AGE) < 2;
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.random.nextInt(5) == 0) {
         int var5 = var1.getValue(AGE);
         if (var5 < 2) {
            var2.setBlock(var3, var1.setValue(AGE, Integer.valueOf(var5 + 1)), 2);
         }
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.relative(var1.getValue(FACING)));
      return var4.is(BlockTags.JUNGLE_LOGS);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      int var5 = var1.getValue(AGE);
      switch((Direction)var1.getValue(FACING)) {
         case SOUTH:
            return SOUTH_AABB[var5];
         case NORTH:
         default:
            return NORTH_AABB[var5];
         case WEST:
            return WEST_AABB[var5];
         case EAST:
            return EAST_AABB[var5];
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();

      for(Direction var8 : var1.getNearestLookingDirections()) {
         if (var8.getAxis().isHorizontal()) {
            var2 = var2.setValue(FACING, var8);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == var1.getValue(FACING) && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return var3.getValue(AGE) < 2;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlock(var3, var4.setValue(AGE, Integer.valueOf(var4.getValue(AGE) + 1)), 2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, AGE);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
