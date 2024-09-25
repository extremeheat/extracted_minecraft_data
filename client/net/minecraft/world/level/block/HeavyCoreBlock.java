package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeavyCoreBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<HeavyCoreBlock> CODEC = simpleCodec(HeavyCoreBlock::new);
   private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public HeavyCoreBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public MapCodec<HeavyCoreBlock> codec() {
      return CODEC;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var2.is(Fluids.WATER)));
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