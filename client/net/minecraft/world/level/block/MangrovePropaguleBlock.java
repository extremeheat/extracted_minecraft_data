package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.grower.MangroveTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MangrovePropaguleBlock extends SaplingBlock implements SimpleWaterloggedBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
   public static final int MAX_AGE = 4;
   private static final VoxelShape[] SHAPE_PER_AGE = new VoxelShape[]{
      Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0),
      Block.box(7.0, 10.0, 7.0, 9.0, 16.0, 9.0),
      Block.box(7.0, 7.0, 7.0, 9.0, 16.0, 9.0),
      Block.box(7.0, 3.0, 7.0, 9.0, 16.0, 9.0),
      Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
   };
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   private static final float GROW_TALL_MANGROVE_PROBABILITY = 0.85F;

   public MangrovePropaguleBlock(BlockBehaviour.Properties var1) {
      super(new MangroveTreeGrower(0.85F), var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(STAGE, Integer.valueOf(0))
            .setValue(AGE, Integer.valueOf(0))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
            .setValue(HANGING, Boolean.valueOf(false))
      );
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return super.mayPlaceOn(var1, var2, var3) || var1.is(Blocks.CLAY);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      return super.getStateForPlacement(var1).setValue(WATERLOGGED, Boolean.valueOf(var3)).setValue(AGE, Integer.valueOf(4));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      VoxelShape var6;
      if (!var1.getValue(HANGING)) {
         var6 = SHAPE_PER_AGE[4];
      } else {
         var6 = SHAPE_PER_AGE[var1.getValue(AGE)];
      }

      return var6.move(var5.x, var5.y, var5.z);
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return isHanging(var1) ? var2.getBlockState(var3.above()).is(Blocks.MANGROVE_LEAVES) : super.canSurvive(var1, var2, var3);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2 == Direction.UP && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!isHanging(var1)) {
         if (var4.nextInt(7) == 0) {
            this.advanceTree(var2, var3, var1, var4);
         }
      } else {
         if (!isFullyGrown(var1)) {
            var2.setBlock(var3, var1.cycle(AGE), 2);
         }
      }
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return !isHanging(var3) || !isFullyGrown(var3);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return isHanging(var4) ? !isFullyGrown(var4) : super.isBonemealSuccess(var1, var2, var3, var4);
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      if (isHanging(var4) && !isFullyGrown(var4)) {
         var1.setBlock(var3, var4.cycle(AGE), 2);
      } else {
         super.performBonemeal(var1, var2, var3, var4);
      }
   }

   private static boolean isHanging(BlockState var0) {
      return var0.getValue(HANGING);
   }

   private static boolean isFullyGrown(BlockState var0) {
      return var0.getValue(AGE) == 4;
   }

   public static BlockState createNewHangingPropagule() {
      return createNewHangingPropagule(0);
   }

   public static BlockState createNewHangingPropagule(int var0) {
      return Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(HANGING, Boolean.valueOf(true)).setValue(AGE, Integer.valueOf(var0));
   }
}
