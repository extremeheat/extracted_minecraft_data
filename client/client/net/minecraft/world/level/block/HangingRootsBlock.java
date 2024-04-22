package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingRootsBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<HangingRootsBlock> CODEC = simpleCodec(HangingRootsBlock::new);
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(2.0, 10.0, 2.0, 14.0, 16.0, 14.0);

   @Override
   public MapCodec<HangingRootsBlock> codec() {
      return CODEC;
   }

   protected HangingRootsBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      if (var2 != null) {
         FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
         return var2.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
      } else {
         return null;
      }
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.above();
      BlockState var5 = var2.getBlockState(var4);
      return var5.isFaceSturdy(var2, var4, Direction.DOWN);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && !this.canSurvive(var1, var4, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var1.getValue(WATERLOGGED)) {
            var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }
}